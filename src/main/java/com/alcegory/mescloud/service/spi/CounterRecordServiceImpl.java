package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.CounterRecordConverter;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;
import com.alcegory.mescloud.repository.CounterRecordRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
@Log
public class CounterRecordServiceImpl implements CounterRecordService {

    private static final int INITIAL_COMPUTED_VALUE = 0;
    private static final int ROLLOVER_OFFSET = 1;
    private static final int ROLLOVER_MAX_VALUE = 65535;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;
    private final ProductionOrderService productionOrderService;
    private final ProductionOrderRepository productionOrderRepository;
    private final CountingEquipmentService countingEquipmentService;
    private final FactoryService factoryService;


    @Override
    public List<CounterRecordDto> filterConclusionRecordsKpi(KpiFilterDto filter) {
        List<CounterRecordConclusionEntity> counterRecordConclusionEntities = repository.findLastPerProductionOrder(filter);
        return converter.conclusionViewToDto(counterRecordConclusionEntities);
    }

    @Override
    public PaginatedCounterRecordsDto filterConclusionRecordsPaginated(CounterRecordFilter filter) {
        int requestedRecords = filter.getTake();
        filter.setTake(filter.getTake() + 1);

        List<CounterRecordConclusionEntity> counterRecordConclusionEntities = repository.findLastPerProductionOrder(filter);
        boolean hasNextPage = counterRecordConclusionEntities.size() > requestedRecords;

        if (hasNextPage) {
            counterRecordConclusionEntities.remove(counterRecordConclusionEntities.size() - 1);
        }

        List<CounterRecordDto> counterRecords = converter.conclusionViewToDto(counterRecordConclusionEntities);

        PaginatedCounterRecordsDto paginatedCounterRecords = new PaginatedCounterRecordsDto();
        paginatedCounterRecords.setHasNextPage(hasNextPage);
        paginatedCounterRecords.setCounterRecords(counterRecords);

        return paginatedCounterRecords;
    }

    @Override
    public PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilter filterDto) {
        int requestedRecords = filterDto.getTake();
        filterDto.setTake(filterDto.getTake() + 1);

        List<CounterRecordEntity> counterRecordEntities = repository.getFilteredAndPaginated(filterDto);
        boolean hasNextPage = counterRecordEntities.size() > requestedRecords;

        if (hasNextPage) {
            counterRecordEntities.remove(counterRecordEntities.size() - 1);
        }

        List<CounterRecordDto> counterRecords = converter.toDto(counterRecordEntities);

        PaginatedCounterRecordsDto paginatedCounterRecords = new PaginatedCounterRecordsDto();
        paginatedCounterRecords.setHasNextPage(hasNextPage);
        paginatedCounterRecords.setCounterRecords(counterRecords);

        return paginatedCounterRecords;
    }

    private boolean isValid(PlcMqttDto equipmentCounts) {

        Optional<CountingEquipmentDto> countingEquipmentOpt =
                countingEquipmentService.findByCode(equipmentCounts.getEquipmentCode());

        return countingEquipmentOpt.isPresent() &&
                equipmentCounts.getCounters().length == countingEquipmentOpt.get().getOutputs().size();
    }

    @Override
    public List<CounterRecordDto> processCounterRecord(PlcMqttDto equipmentCountsMqttDto) {

        if (!isValid(equipmentCountsMqttDto)) {
            log.warning(() -> String.format("Received counts are invalid either because no Counting Equipment was found " +
                    "with the code [%s] or because received equipment outputs number [%s] does not match " +
                    "the Counting Equipment outputs number", equipmentCountsMqttDto.getEquipmentCode(), equipmentCountsMqttDto.getCounters().length));
            return Collections.emptyList();
        }

        List<CounterRecordEntity> counterRecords = new ArrayList<>(equipmentCountsMqttDto.getCounters().length);
        for (CounterMqttDto counterMqttDto : equipmentCountsMqttDto.getCounters()) {
            CounterRecordEntity counterRecord = extractCounterRecordEntity(counterMqttDto, equipmentCountsMqttDto);
            counterRecords.add(counterRecord);
        }

        return saveAll(counterRecords);
    }

    private CounterRecordEntity extractCounterRecordEntity(CounterMqttDto counterDto, PlcMqttDto equipmentCountsDto) {

        CounterRecordEntity counterRecord = new CounterRecordEntity();
        //counterRecord.setRegisteredAt(DateUtil.getCurrentTime(factoryService.getTimeZone()));
        counterRecord.setRegisteredAt(new Date());
        counterRecord.setRealValue(counterDto.getValue());
        counterRecord.setActiveTime(equipmentCountsDto.getActiveTime());

        setEquipmentOutput(counterRecord, counterDto.getOutputCode());
        setProductionOrder(counterRecord, equipmentCountsDto.getProductionOrderCode());

        //TODO: we have to check if this validation is correct, considering we can have counter records without PO.s
        CounterRecordEntity lastPersistedCount = getLastPersistedCount(counterRecord);
        if (counterRecord.getProductionOrder() != null) {
            setComputedValue(counterRecord, lastPersistedCount);
        }

        setComputedActiveTime(lastPersistedCount, counterRecord);

        return counterRecord;
    }

    private CounterRecordEntity getLastPersistedCount(CounterRecordEntity counterRecord) {
        return findLastPersistedCount(counterRecord)
                .orElse(null);
    }

    private void setEquipmentOutput(CounterRecordEntity counterRecord, String equipmentOutputCode) {
        Optional<EquipmentOutputDto> equipmentOutputOpt = equipmentOutputService.findByCode(equipmentOutputCode);
        if (equipmentOutputOpt.isEmpty()) {
            log.warning(() -> String.format("No Equipment Output found with the code [%s]", equipmentOutputCode));
            return;
        }

        //TODO: This should rely on a converter
        EquipmentOutputDto equipmentOutput = equipmentOutputOpt.get();
        EquipmentOutputEntity equipmentOutputEntity = new EquipmentOutputEntity();
        equipmentOutputEntity.setId(equipmentOutput.getId());
        counterRecord.setEquipmentOutput(equipmentOutputEntity);
        counterRecord.setEquipmentOutputAlias(equipmentOutput.getAlias().getAlias());
        counterRecord.setIsValidForProduction(equipmentOutput.isValidForProduction());
    }

    private void setProductionOrder(CounterRecordEntity counterRecord, String productionOrderCode) {

        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.findByCode(productionOrderCode);
        if (productionOrderOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found with the code [%s]", productionOrderCode));
            return;
        }

        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setId(productionOrderOpt.get().getId());
        counterRecord.setProductionOrder(productionOrderEntity);
    }

    private void setComputedValue(CounterRecordEntity receivedCount, CounterRecordEntity lastPersistedCount) {

        if (lastPersistedCount == null) {
            receivedCount.setComputedValue(INITIAL_COMPUTED_VALUE);
            return;
        }

        int computedValue = genericCalculate(lastPersistedCount.getRealValue(), receivedCount.getRealValue(),
                lastPersistedCount.getComputedValue());
        int increment = calculateIncrement(lastPersistedCount, receivedCount);
        receivedCount.setIncrement(increment);
        receivedCount.setComputedValue(computedValue);
    }

    private void setComputedActiveTime(CounterRecordEntity lastPersistedCount, CounterRecordEntity counterRecord) {
        log.info(() -> String.format("Set computed active time: [%s]", counterRecord.getActiveTime()));
        if (lastPersistedCount == null) {
            counterRecord.setComputedActiveTime(INITIAL_COMPUTED_VALUE);
            return;
        }

        int updatedComputedActiveTime = genericCalculate(lastPersistedCount.getActiveTime(), counterRecord.getComputedActiveTime(),
                lastPersistedCount.getComputedActiveTime());
        counterRecord.setComputedActiveTime(updatedComputedActiveTime);
    }

    private int genericCalculate(int lastPersisted, int received, int computedPersisted) {

        if (isGenericRollover(lastPersisted, received)) {
            return genericCalculateRollover(lastPersisted, received, computedPersisted);
        }

        return genericIncrement(lastPersisted, received, computedPersisted);
    }

    private boolean isGenericRollover(int lastPersisted, int received) {
        return lastPersisted < received;
    }

    private int genericCalculateRollover(int lastPersisted, int received, int computedPersisted) {
        int totalIncrement = genericRolloverCalculateIncrement(lastPersisted, received);
        return computedPersisted + totalIncrement;
    }

    private int genericRolloverCalculateIncrement(int lastPersisted, int received) {
        int incrementBeforeOverflow = ROLLOVER_MAX_VALUE - lastPersisted;
        return incrementBeforeOverflow + ROLLOVER_OFFSET + received;
    }

    private int genericIncrement(int lastPersisted, int received, int computedPersisted) {
        int increment = genericComputeValueIncrement(lastPersisted, received);
        return computedPersisted + increment;
    }

    private int genericComputeValueIncrement(int lastPersisted, int received) {
        return received - lastPersisted;
    }

    private int calculateIncrement(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {

        if (lastPersistedCount.getRealValue() > receivedCount.getRealValue()) {
            return 0;
        }

        return genericComputeValueIncrement(lastPersistedCount.getRealValue(), receivedCount.getRealValue());
    }

    private Optional<CounterRecordEntity> findLastPersistedCount(CounterRecordEntity counterRecord) {
        Long productionOrderId = counterRecord.getProductionOrder().getId();
        Long equipmentOutputId = counterRecord.getEquipmentOutput().getId();
        return repository.findLastByProductionOrderId(productionOrderId, equipmentOutputId);
    }

    public boolean areValidInitialCounts(String productionOrderCode) {
        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderRepository.findByCode(productionOrderCode);
        return productionOrderOpt.isPresent() &&
                repository.findLastByProductionOrderId(productionOrderOpt.get().getId()).isEmpty();
    }

    public boolean areValidContinuationCounts(String productionOrderCode) {
        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderRepository.findByCode(productionOrderCode);
        return productionOrderOpt.isPresent() &&
                repository.findLastByProductionOrderId(productionOrderOpt.get().getId()).isPresent();
    }

    @Override
    public Integer sumValidCounterIncrement(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter) {
        return repository.sumValidCounterIncrement(countingEquipmentId, startDateFilter, endDateFilter);
    }

    @Override
    public Integer sumCounterIncrement(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter) {
        return repository.sumCounterIncrement(countingEquipmentId, startDateFilter, endDateFilter);
    }

    @Override
    public Long getComputedActiveTimeByProductionOrderId(Long productionOrderId, Timestamp endDate) {
        return repository.getComputedActiveTimeByProductionOrderId(productionOrderId, endDate);
    }

    private List<CounterRecordDto> saveAll(List<CounterRecordEntity> counterRecords) {
        Iterable<CounterRecordEntity> counterRecordEntities = repository.saveAll(counterRecords);
        return converter.toDto(counterRecordEntities);
    }
}
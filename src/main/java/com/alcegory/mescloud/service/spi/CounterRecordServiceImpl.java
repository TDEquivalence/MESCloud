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

        int computedValue = calculateComputedValue(lastPersistedCount, receivedCount);
        int increment = calculateIncrement(lastPersistedCount, receivedCount);
        receivedCount.setIncrement(increment);
        receivedCount.setComputedValue(computedValue);
    }

    private Optional<CounterRecordEntity> findLastPersistedCount(CounterRecordEntity counterRecord) {
        Long productionOrderId = counterRecord.getProductionOrder().getId();
        Long equipmentOutputId = counterRecord.getEquipmentOutput().getId();
        return repository.findLastByProductionOrderId(productionOrderId, equipmentOutputId);
    }

    private int calculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {

        if (isRollover(lastPersistedCount, receivedCount)) {
            return rolloverCalculateComputedValue(lastPersistedCount, receivedCount);
        }

        return defaultCalculateComputedValue(lastPersistedCount, receivedCount);
    }

    private boolean isRollover(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        return receivedCount.getRealValue() < lastPersistedCount.getRealValue();
    }

    private int rolloverCalculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        int totalIncrement = rolloverCalculateIncrement(lastPersistedCount, receivedCount);
        return lastPersistedCount.getComputedValue() + totalIncrement;
    }

    private int rolloverCalculateIncrement(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        int incrementBeforeOverflow = ROLLOVER_MAX_VALUE - lastPersistedCount.getRealValue();
        return incrementBeforeOverflow + ROLLOVER_OFFSET + receivedCount.getRealValue();
    }

    private int defaultCalculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        int computedValueIncrement = computeValueIncrement(lastPersistedCount, receivedCount);
        return lastPersistedCount.getComputedValue() + computedValueIncrement;
    }

    private int computeValueIncrement(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        return receivedCount.getRealValue() - lastPersistedCount.getRealValue();
    }

    private int calculateIncrement(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {

        if (lastPersistedCount.getRealValue() > receivedCount.getRealValue()) {
            return 0;
        }

        return computeValueIncrement(lastPersistedCount, receivedCount);
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

    private void setComputedActiveTime(CounterRecordEntity lastPersistedCount, CounterRecordEntity counterRecord) {
        log.info(() -> String.format("Set computed active time: [%s]", counterRecord.getActiveTime()));
        if (lastPersistedCount == null) {
            counterRecord.setComputedActiveTime(INITIAL_COMPUTED_VALUE);
            return;
        }

        long updatedComputedActiveTime = calculateUpdatedActiveTime(lastPersistedCount, counterRecord);
        counterRecord.setComputedActiveTime(updatedComputedActiveTime);
    }

    private long calculateUpdatedActiveTime(CounterRecordEntity lastPersistedCount, CounterRecordEntity counterRecord) {
        log.info(() -> String.format("Calculate active time, last persisted: [%s]", lastPersistedCount.getActiveTime()));
        log.info(() -> String.format("Calculate received time, received: [%s]", counterRecord.getActiveTime()));

        if (isActiveTimeRollover(lastPersistedCount, counterRecord)) {
            return calculateRolloverActiveTime(lastPersistedCount.getActiveTime(), counterRecord.getActiveTime());
        }

        return incrementActiveTime(lastPersistedCount, counterRecord);
    }

    private boolean isActiveTimeRollover(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        return receivedCount.getActiveTime() < lastPersistedCount.getActiveTime();
    }

    private long calculateRolloverActiveTime(long persistedActiveTime, long receivedActiveTime) {
        log.info(() -> String.format("Calculate active time rollover, last persisted: [%s]", persistedActiveTime));
        log.info(() -> String.format("Calculate received time rollover, received: [%s]", receivedActiveTime));
        long remainingActiveTime = ROLLOVER_MAX_VALUE - persistedActiveTime;
        return persistedActiveTime + remainingActiveTime  + receivedActiveTime + ROLLOVER_OFFSET;
    }

    private long incrementActiveTime(CounterRecordEntity lastPersistedCount, CounterRecordEntity counterRecord) {
      long increment = lastPersistedCount.getActiveTime() - counterRecord.getActiveTime();

        log.info(() -> String.format("Increment active time: [%s]", lastPersistedCount.getComputedValue() + increment));
      return lastPersistedCount.getComputedValue() + increment;
    }
}
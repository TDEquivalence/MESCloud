package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.*;
import com.tde.mescloud.model.dto.filter.SearchableProperty;
import com.tde.mescloud.model.entity.CounterRecordConclusionEntity;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.CounterRecordRepository;
import com.tde.mescloud.repository.ProductionOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class CounterRecordServiceImpl implements CounterRecordService {

    private static final int INITIAL_COMPUTED_VALUE = 0;
    private static final int ROLLOVER_OFFSET = 1;
    private static final int PL_UINT_MAX_VALUE = 65535;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;
    private final ProductionOrderService productionOrderService;
    private final ProductionOrderRepository productionOrderRepository;

    private final CountingEquipmentService countingEquipmentService;

    @Override
    public List<CounterRecordDto> winnowConclusionRecordsKpi(KpiFilterDto filter) {
        List<CounterRecordConclusionEntity> counterRecordConclusionEntities = repository.findLastPerProductionOrder(filter);
        return converter.conclusionViewToDto(counterRecordConclusionEntities);
    }

    @Override
    public PaginatedCounterRecordsDto winnowConclusionRecordsPaginated(CounterRecordFilterDto filter) {
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
    public PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilterDto filterDto) {
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
    public List<CounterRecordDto> save(PlcMqttDto equipmentCountsMqttDto) {

        if (!isValid(equipmentCountsMqttDto)) {
            log.warning(() -> String.format("Received counts are invalid either because no Counting Equipment was found with the code [%s] or because received equipment outputs number [%s] does not match the Counting Equipment outputs number", equipmentCountsMqttDto.getEquipmentCode(), equipmentCountsMqttDto.getCounters().length));
            return null;
        }

        List<CounterRecordEntity> counterRecords = new ArrayList<>(equipmentCountsMqttDto.getCounters().length);
        for (CounterMqttDto counterMqttDto : equipmentCountsMqttDto.getCounters()) {
            CounterRecordEntity counterRecord = extractCounterRecordEntity(counterMqttDto, equipmentCountsMqttDto);
            counterRecords.add(counterRecord);
        }

        Iterable<CounterRecordEntity> counterRecordEntities = repository.saveAll(counterRecords);
        return converter.toDto(counterRecordEntities);
    }

    private CounterRecordEntity extractCounterRecordEntity(CounterMqttDto counterDto, PlcMqttDto equipmentCountsDto) {

        CounterRecordEntity counterRecord = new CounterRecordEntity();
        counterRecord.setRegisteredAt(new Date());
        counterRecord.setRealValue(counterDto.getValue());

        setEquipmentOutput(counterRecord, counterDto.getOutputCode());
        setProductionOrder(counterRecord, equipmentCountsDto.getProductionOrderCode());

        if (counterRecord.getProductionOrder() != null) {
            setComputedValue(counterRecord);
        }

        return counterRecord;
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
        counterRecord.setEquipmentOutputAlias(equipmentOutput.getAlias());
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

    private void setComputedValue(CounterRecordEntity receivedCount) {

        Optional<CounterRecordEntity> lastPersistedCountOpt = findLastPersistedCount(receivedCount);
        if (lastPersistedCountOpt.isEmpty()) {
            receivedCount.setComputedValue(INITIAL_COMPUTED_VALUE);
            return;
        }

        int computedValue = calculateComputedValue(lastPersistedCountOpt.get(), receivedCount);
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
        int incrementBeforeOverflow = PL_UINT_MAX_VALUE - lastPersistedCount.getRealValue();
        int totalIncrement = incrementBeforeOverflow + ROLLOVER_OFFSET + receivedCount.getRealValue();
        return lastPersistedCount.getComputedValue() + totalIncrement;
    }

    private int defaultCalculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        int computedValueIncrement = receivedCount.getRealValue() - lastPersistedCount.getRealValue();
        return lastPersistedCount.getComputedValue() + computedValueIncrement;
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
}
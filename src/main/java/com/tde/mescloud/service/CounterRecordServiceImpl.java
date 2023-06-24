package com.tde.mescloud.service;

import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterMqttDto;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
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

    private final int INITIAL_COMPUTED_VALUE = 0;
    private final int ROLLOVER_OFFSET = 1;
    private final int PL_UINT_MAX_VALUE = 65535;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;
    private final ProductionOrderService productionOrderService;
    private final ProductionOrderRepository productionOrderRepository;


    @Override
    public List<CounterRecordDto> findAll() {
        Iterable<CounterRecordEntity> counterRecordEntities = repository.findAll();
        return converter.toDto(counterRecordEntities);
    }

    @Override
    public List<CounterRecordDto> findLastPerProductionOrder(CounterRecordFilterDto filter) {
        Iterable<CounterRecordEntity> counterRecordEntities = repository.findLastPerProductionOrder(filter);
        return converter.toDto(counterRecordEntities);
    }

    @Override
    public List<CounterRecordDto> findAllByCriteria(CounterRecordFilterDto filterDto) {
        List<CounterRecordEntity> counterRecordEntities = repository.findByCriteria(filterDto);
        return converter.toDto(counterRecordEntities);
    }

    @Override
    public void save(EquipmentCountsMqttDto equipmentCountsMqttDto) {

        List<CounterRecordEntity> counterRecords = new ArrayList<>(equipmentCountsMqttDto.getCounters().length);
        for (CounterMqttDto counterMqttDto : equipmentCountsMqttDto.getCounters()) {
            CounterRecordEntity counterRecord = extractCounterRecordEntity(counterMqttDto, equipmentCountsMqttDto);
            counterRecords.add(counterRecord);
        }

        repository.saveAll(counterRecords);
    }

    private CounterRecordEntity extractCounterRecordEntity(CounterMqttDto counterDto, EquipmentCountsMqttDto equipmentCountsDto) {

        CounterRecordEntity counterRecord = new CounterRecordEntity();
        counterRecord.setRegisteredAt(new Date());
        counterRecord.setRealValue(counterDto.getValue());

        setEquipmentOutput(counterRecord, counterDto.getOutputCode());
        setProductionOrder(counterRecord, equipmentCountsDto.getProductionOrderCode());
        setComputedValue(counterRecord);

        return counterRecord;
    }

    //TODO: Discuss MQTT Protocol -> MC-80 .2
    private void setEquipmentOutput(CounterRecordEntity counterRecord, String equipmentOutputCode) {
        EquipmentOutput equipmentOutput = equipmentOutputService.findByCode(equipmentOutputCode);
        EquipmentOutputEntity equipmentOutputEntity = new EquipmentOutputEntity();
        equipmentOutputEntity.setId(equipmentOutput.getId());

        counterRecord.setEquipmentOutput(equipmentOutputEntity);
        counterRecord.setEquipmentOutputAlias(equipmentOutput.getAlias());
    }

    //TODO: Discuss MQTT Protocol -> MC-80 .2
    private void setProductionOrder(CounterRecordEntity counterRecord, String productionOrderCode) {
        ProductionOrder productionOrder = productionOrderService.findByCode(productionOrderCode);
        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setId(productionOrder.getId());

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
        return repository.findLast(productionOrderId, equipmentOutputId);
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
        ProductionOrderEntity productionOrderEntity = productionOrderRepository.findByCode(productionOrderCode);
        return productionOrderEntity != null &&
                repository.findLast(productionOrderEntity.getId()).isEmpty();
    }

    public boolean areValidContinuationCounts(String productionOrderCode) {
        ProductionOrderEntity productionOrderEntity = productionOrderRepository.findByCode(productionOrderCode);
        return productionOrderEntity != null &&
                repository.findLast(productionOrderEntity.getId()).isPresent();
    }
}
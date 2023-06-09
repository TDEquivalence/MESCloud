package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterMqttDto;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;
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
    private final int PLC_UINT_OVERFLOW = 65535;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;
    private final ProductionOrderService productionOrderService;
    private final ProductionOrderRepository productionOrderRepository;


    @Override
    public List<CounterRecord> findAll() {
        Iterable<CounterRecordEntity> counterRecordEntities = repository.findAll();
        return converter.convertToDomainObj(counterRecordEntities);
    }

    public List<CounterRecord> findAllByCriteria(CounterRecordFilterDto filterDto) {
        List<CounterRecordEntity> counterRecordEntities = repository.findByCriteria(filterDto);
        return converter.convertToDomainObj(counterRecordEntities);
    }

    @Override
    public List<CounterRecord> save(EquipmentCountsMqttDto equipmentCountsDto) {

        List<CounterRecordEntity> counterRecordEntities = new ArrayList<>(equipmentCountsDto.getCounters().length);
        for (CounterMqttDto counterDto : equipmentCountsDto.getCounters()) {
            CounterRecord counterRecord = extractCounterRecord(counterDto, equipmentCountsDto);
            CounterRecordEntity counterRecordEntity = converter.convertToEntity(counterRecord);
            counterRecordEntities.add(counterRecordEntity);
        }

        List<CounterRecordEntity> persistedCounterRecords = (List<CounterRecordEntity>) repository.saveAll(counterRecordEntities);
        return converter.convertToDomainObj(persistedCounterRecords);
    }

    private CounterRecord extractCounterRecord(CounterMqttDto counterDto, EquipmentCountsMqttDto equipmentCountsDto) {

        CounterRecord counterRecord = converter.convertToDomainObj(equipmentCountsDto, counterDto);
        setEquipmentOutput(counterRecord, counterDto.getOutputCode());
        setProductionOrder(counterRecord, equipmentCountsDto.getProductionOrderCode());
        setComputedValue(counterRecord);
        counterRecord.setRegisteredAt(new Date());

        return counterRecord;
    }

    private void setEquipmentOutput(CounterRecord counterRecord, String equipmentOutputCode) {
        EquipmentOutput equipmentOutput = equipmentOutputService.findByCode(equipmentOutputCode);
        counterRecord.setEquipmentOutput(equipmentOutput);
    }

    private void setProductionOrder(CounterRecord counterRecord, String productionOrderCode) {
        //TODO: Discuss MQTT Protocol -> MC-80 .2
        ProductionOrder productionOrder = productionOrderService.findByCode(productionOrderCode);
        counterRecord.setProductionOrder(productionOrder);
    }

    private void setComputedValue(CounterRecord receivedCount) {

        Optional<CounterRecordEntity> lastPersistedCountOpt = findLastPersistedCount(receivedCount);
        if (lastPersistedCountOpt.isEmpty()) {
            receivedCount.setComputedValue(INITIAL_COMPUTED_VALUE);
            return;
        }

        int computedValue = calculateComputedValue(lastPersistedCountOpt.get(), receivedCount);
        receivedCount.setComputedValue(computedValue);
    }

    private Optional<CounterRecordEntity> findLastPersistedCount(CounterRecord counterRecord) {
        long productionOrderId = counterRecord.getProductionOrder().getId();
        long equipmentOutputId = counterRecord.getEquipmentOutput().getId();
        return repository.findLast(productionOrderId, equipmentOutputId);
    }

    private int calculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecord receivedCount) {

        if (isRollover(lastPersistedCount, receivedCount)) {
            return rolloverCalculateComputedValue(lastPersistedCount, receivedCount);
        }

        return defaultCalculateComputedValue(lastPersistedCount, receivedCount);
    }

    private boolean isRollover(CounterRecordEntity lastPersistedCount, CounterRecord receivedCount) {
        return receivedCount.getRealValue() < lastPersistedCount.getRealValue();
    }

    private int rolloverCalculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecord receivedCount) {
        int incrementBeforeOverflow = PLC_UINT_OVERFLOW - lastPersistedCount.getRealValue();
        return incrementBeforeOverflow + receivedCount.getRealValue();
    }

    private int defaultCalculateComputedValue(CounterRecordEntity lastPersistedCount, CounterRecord receivedCount) {
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
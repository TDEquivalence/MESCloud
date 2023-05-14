package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterMqttDTO;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.repository.CounterRecordRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log
public class CounterRecordServiceImpl implements CounterRecordService {

    private final int INITIAL_COMPUTED_VALUE = 0;
    private final int PLC_UINT_OVERFLOW = 65535;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;
    private final ProductionOrderService productionOrderService;

    public CounterRecordServiceImpl(CounterRecordConverter converter,
                                    CounterRecordRepository repository,
                                    EquipmentOutputService equipmentOutputService,
                                    ProductionOrderService productionOrderService) {
        this.converter = converter;
        this.repository = repository;
        this.equipmentOutputService = equipmentOutputService;
        this.productionOrderService = productionOrderService;
    }

    //TODO: Improve efficiency to avoid the loop in this method and save(List<CounterRecord>)
    public List<CounterRecord> save(EquipmentCountsMqttDTO equipmentCountsDTO) {

        List<CounterRecord> counterRecords = new ArrayList<>(equipmentCountsDTO.getCounters().length);
        for (CounterMqttDTO counterDTO : equipmentCountsDTO.getCounters()) {
            CounterRecord counterRecord = converter.convertToDO(equipmentCountsDTO, counterDTO);
            setEquipmentOutput(counterRecord, counterDTO.getOutputCode());
            setProductionOrder(counterRecord, equipmentCountsDTO.getProductionOrderCode());
            setComputedValue(counterRecord);
            counterRecord.setRegisteredAt(new Date());
            counterRecords.add(counterRecord);
        }

        return save(counterRecords);
    }

    private void setEquipmentOutput(CounterRecord counterRecord, String equipmentOutputCode) {
        EquipmentOutput equipmentOutput = equipmentOutputService.findByCode(equipmentOutputCode);
        counterRecord.setEquipmentOutput(equipmentOutput);
    }

    private void setProductionOrder(CounterRecord counterRecord, String productionOrderCode) {
        //Sending the ProductionOrder ID instead of its code, on the MqttDTO, would save a DB read operation
        ProductionOrder productionOrder = productionOrderService.findByCode(productionOrderCode);
        counterRecord.setProductionOrder(productionOrder);
    }

    private void setComputedValue(CounterRecord receivedCounterRecord) {

        if (receivedCounterRecord == null) {
            String msg = "Unable to compute Counter Record value: null counter record";
            log.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        CounterRecordEntity lastPersistedCounterRecord = findLastCounterRecord(receivedCounterRecord);
        int computedValue = lastPersistedCounterRecord == null ?
                INITIAL_COMPUTED_VALUE : calculateComputedValue(lastPersistedCounterRecord, receivedCounterRecord);
        receivedCounterRecord.setComputedValue(computedValue);
    }

    private CounterRecordEntity findLastCounterRecord(CounterRecord counterRecord) {
        long productionOrderId = counterRecord.getProductionOrder().getId();
        long equipmentOutputId = counterRecord.getEquipmentOutput().getId();
        return repository.findLast(productionOrderId, equipmentOutputId);
    }

    private int calculateComputedValue(CounterRecordEntity lastPersistedCounterRecord,
                                       CounterRecord receivedCounterRecord) {

        int computedValue;
        if (receivedCounterRecord.getRealValue() < lastPersistedCounterRecord.getRealValue()) {
            int incrementBeforeOverflow = PLC_UINT_OVERFLOW - lastPersistedCounterRecord.getRealValue();
            computedValue = incrementBeforeOverflow + receivedCounterRecord.getRealValue();
        } else {
            int lastComputedValue = lastPersistedCounterRecord.getComputedValue();
            int computedValueIncrement = receivedCounterRecord.getRealValue() - lastPersistedCounterRecord.getRealValue();
            computedValue = lastComputedValue + computedValueIncrement;
        }

        return computedValue;
    }

    @Override
    public List<CounterRecord> save(List<CounterRecord> counterRecords) {
        List<CounterRecordEntity> counterRecordEntities = new ArrayList<>(counterRecords.size());
        for (CounterRecord counterRecord : counterRecords) {
            CounterRecordEntity counterRecordEntity = converter.convertToEntity(counterRecord);
            counterRecordEntities.add(counterRecordEntity);
        }
        List<CounterRecordEntity> persistedCounterRecords = (List<CounterRecordEntity>) repository.saveAll(counterRecordEntities);
        return converter.convertToDO(persistedCounterRecords);
    }
}

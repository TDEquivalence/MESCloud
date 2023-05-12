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
            counterRecord.setRegisteredAt(new Date());

            EquipmentOutput equipmentOutput = equipmentOutputService.findByCode(counterDTO.getOutputCode());
            counterRecord.setEquipmentOutput(equipmentOutput);
            //TODO: Discuss replacing the PO code with the IDs, considering it would save a read operation on the DB
            ProductionOrder productionOrder = productionOrderService.findByCode(equipmentCountsDTO.getProductionOrderCode());
            counterRecord.setProductionOrder(productionOrder);
            //TODO: Refactor
            counterRecord.setComputedValue(getComputedValue(counterRecord));
            counterRecords.add(counterRecord);
        }
        return save(counterRecords);
    }

    private int getComputedValue(CounterRecord counterRecord) {

        if (counterRecord == null) {
            String msg = "Unable to compute Counter Record value: null counter record";
            log.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        CounterRecordEntity counterRecordEntity = findLastCounterRecord(counterRecord);
        return counterRecordEntity == null ? INITIAL_COMPUTED_VALUE : getComputedValue(counterRecordEntity, counterRecord);
    }

    private CounterRecordEntity findLastCounterRecord(CounterRecord counterRecord) {

        long productionOrderId = counterRecord.getProductionOrder().getId();
        long equipmentOutputId = counterRecord.getEquipmentOutput().getId();
        return repository.findLast(productionOrderId, equipmentOutputId);
    }

    private int getComputedValue(CounterRecordEntity counterRecordEntity, CounterRecord counterRecord) {

        int computedValue;
        if (counterRecord.getRealValue() < counterRecordEntity.getRealValue()) {

            int incrementBeforeOverflow = PLC_UINT_OVERFLOW - counterRecordEntity.getRealValue();
            computedValue = incrementBeforeOverflow + counterRecord.getRealValue();
        } else {
            int previous = counterRecordEntity.getComputedValue();
            int difference = counterRecord.getRealValue() - counterRecordEntity.getRealValue();
            computedValue = previous + difference;
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

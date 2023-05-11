package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterMqttDTO;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.repository.CounterRecordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CounterRecordServiceImpl implements CounterRecordService {

    private final int INITIAL_COMPUTED_VALUE = 0;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;

    public CounterRecordServiceImpl(CounterRecordConverter converter,
                                    CounterRecordRepository repository,
                                    EquipmentOutputService equipmentOutputService) {
        this.converter = converter;
        this.repository = repository;
        this.equipmentOutputService = equipmentOutputService;
    }

    //TODO: Improve efficiency to avoid the loop in this method and save(List<CounterRecord>)
    public List<CounterRecord> saveProductionOrderInitialCounts(EquipmentCountsMqttDTO equipmentCountsDTO) {
        List<CounterRecord> counterRecords = new ArrayList<>(equipmentCountsDTO.getCounters().length);
        for (CounterMqttDTO counterDTO : equipmentCountsDTO.getCounters()) {
            CounterRecord counterRecord = converter.convertToDO(equipmentCountsDTO, counterDTO);
            counterRecord.setComputedValue(INITIAL_COMPUTED_VALUE);
            counterRecord.setRegisteredAt(new Date());

            EquipmentOutput equipmentOutput = equipmentOutputService.findByCode(counterDTO.getOutputCode());
            counterRecord.setEquipmentOutput(equipmentOutput);
            //TODO: get productionOrder (id) from productionOrderCode (productionOrderService)
            //TODO: Discuss replacing the PO code with the IDs, considering it would save a read operation on the DB

            counterRecords.add(counterRecord);
        }
        return save(counterRecords);
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

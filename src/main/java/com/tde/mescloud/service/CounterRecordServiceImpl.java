package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.repository.CounterRecordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CounterRecordServiceImpl implements CounterRecordService {

    private static final int ALIAS_MIN_LENGTH = 1;
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


    @Override
    public List<CounterRecord> save(List<CounterRecord> counterRecords) {
        List<CounterRecordEntity> counterRecordEntities = new ArrayList<>(counterRecords.size());
        for (CounterRecord counterRecord : counterRecords) {
            if (hasOutputAlias(counterRecord)) {
                String equipmentOutputAlias = equipmentOutputService.getOutputAlias(counterRecord.getEquipmentOutputCode());
                counterRecord.setEquipmentOutputAlias(equipmentOutputAlias);
            }
            //TODO: Implement convertToEntity
            CounterRecordEntity counterRecordEntity = converter.convertToEntity(counterRecord);
            counterRecordEntities.add(counterRecordEntity);
        }
        List<CounterRecordEntity> persistedCounterRecords = (List<CounterRecordEntity>) repository.saveAll(counterRecordEntities);
        return converter.convertToDO(persistedCounterRecords);
    }

    private boolean hasOutputAlias(CounterRecord counterRecord) {
        return counterRecord.getEquipmentOutputAlias() != null &&
                counterRecord.getEquipmentOutputAlias().length() > ALIAS_MIN_LENGTH;
    }
}

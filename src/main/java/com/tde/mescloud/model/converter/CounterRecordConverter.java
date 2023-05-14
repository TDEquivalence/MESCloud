package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.dto.CounterMqttDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;

import java.util.ArrayList;
import java.util.List;

public interface CounterRecordConverter {

    CounterRecord convertToDO(EquipmentCountsMqttDto equipmentCountsDTO, CounterMqttDto counterDTO);

    CounterRecord convertToDO(CounterRecordEntity entity);

    default List<CounterRecord> convertToDO(List<CounterRecordEntity> counterRecordEntities) {
        List<CounterRecord> counterRecords = new ArrayList<>(counterRecordEntities.size());
        for (CounterRecordEntity entity : counterRecordEntities) {
            counterRecords.add(convertToDO(entity));
        }
        return counterRecords;
    }

    CounterRecordEntity convertToEntity(CounterRecord counterRecord);
}

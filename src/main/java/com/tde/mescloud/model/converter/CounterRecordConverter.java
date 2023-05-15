package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.dto.CounterMqttDto;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;

import java.util.ArrayList;
import java.util.List;

public interface CounterRecordConverter {

    CounterRecord convertToDomainObj(EquipmentCountsMqttDto equipmentCountsDTO, CounterMqttDto counterDTO);

    CounterRecord convertToDomainObj(CounterRecordEntity entity);

    List<CounterRecordDto> convertToDto(List<CounterRecord> counterRecords);

    default List<CounterRecord> convertToDomainObj(List<CounterRecordEntity> counterRecordEntities) {
        List<CounterRecord> counterRecords = new ArrayList<>(counterRecordEntities.size());
        for (CounterRecordEntity entity : counterRecordEntities) {
            counterRecords.add(convertToDomainObj(entity));
        }
        return counterRecords;
    }

    CounterRecordEntity convertToEntity(CounterRecord counterRecord);
}

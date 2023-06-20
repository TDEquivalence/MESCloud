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

    default List<CounterRecord> convertToDomainObj(Iterable<CounterRecordEntity> counterRecordEntities) {
        List<CounterRecord> counterRecords = new ArrayList<>();
        for (CounterRecordEntity entity : counterRecordEntities) {
            CounterRecord counterRecord = convertToDomainObj(entity);
            counterRecords.add(counterRecord);
        }
        return counterRecords;
    }

    CounterRecordDto convertToDto(CounterRecord counterRecord);

    default List<CounterRecordDto> convertToDto(List<CounterRecord> counterRecords) {
        return counterRecords.stream().map(this::convertToDto).toList();
    }

    CounterRecordEntity convertToEntity(CounterRecord counterRecord);

    CounterRecordDto toDto(CounterRecordEntity entity);

    default List<CounterRecordDto> toDto(Iterable<CounterRecordEntity> entities) {
        List<CounterRecordDto> dtos = new ArrayList<>();
        entities.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }
}

package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordDailySummaryEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordDetailedSummaryEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordEntity;

import java.util.ArrayList;
import java.util.List;

public interface CounterRecordConverter {

    CounterRecordDto toDto(CounterRecordEntity entity);

    CounterRecordDto toDto(CounterRecordDailySummaryEntity entity);

    CounterRecordDto toDto(CounterRecordDetailedSummaryEntity entity);

    List<CounterRecordDto> toDtoList(List<CounterRecordDailySummaryEntity> entityList);

    List<CounterRecordDto> toDtoDetailedList(List<CounterRecordDetailedSummaryEntity> entityList);

    default List<CounterRecordDto> toDto(Iterable<CounterRecordEntity> entities) {
        List<CounterRecordDto> dtos = new ArrayList<>();
        entities.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    CounterRecordDto conclusionViewToDto(CounterRecordConclusionEntity entity);

    default List<CounterRecordDto> conclusionViewToDto(Iterable<CounterRecordConclusionEntity> entities) {
        List<CounterRecordDto> dtos = new ArrayList<>();
        entities.forEach(entity -> dtos.add(conclusionViewToDto(entity)));
        return dtos;
    }
}

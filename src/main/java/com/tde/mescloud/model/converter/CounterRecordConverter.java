package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;

import java.util.ArrayList;
import java.util.List;

public interface CounterRecordConverter {

    CounterRecordDto toDto(CounterRecordEntity entity);

    default List<CounterRecordDto> toDto(Iterable<CounterRecordEntity> entities) {
        List<CounterRecordDto> dtos = new ArrayList<>();
        entities.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }
}

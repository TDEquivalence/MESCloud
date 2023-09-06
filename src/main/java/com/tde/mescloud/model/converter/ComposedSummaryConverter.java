package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ComposedSummaryDto;
import com.tde.mescloud.model.entity.ComposedSummaryEntity;

import java.util.ArrayList;
import java.util.List;

public interface ComposedSummaryConverter {

    ComposedSummaryDto toDto(ComposedSummaryEntity entity);

    default List<ComposedSummaryDto> toDto(List<ComposedSummaryEntity> entities) {
        List<ComposedSummaryDto> dtos = new ArrayList<>(entities.size());
        entities.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }
}

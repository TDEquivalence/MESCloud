package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.dto.BatchDto;
import com.tde.mescloud.model.entity.BatchEntity;

import java.util.List;

public interface BatchConverter {

    BatchEntity toEntity(BatchDto dto);

    BatchDto toDto(BatchEntity batch);

    default List<BatchDto> toDto(List<BatchEntity> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    default List<BatchEntity> toEntity(List<BatchDto> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }
}

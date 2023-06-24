package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.repository.CountingEquipmentProjection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CountingEquipmentConverter {

    CountingEquipmentDto toDto(CountingEquipmentEntity entity);

    CountingEquipmentDto toDto(CountingEquipmentProjection projection);

    default List<CountingEquipmentDto> projectionToDto(Iterable<CountingEquipmentProjection> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

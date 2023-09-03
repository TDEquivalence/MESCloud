package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ImsDto;
import com.tde.mescloud.model.entity.ImsEntity;

import java.util.ArrayList;
import java.util.List;

public interface ImsConverter {

    ImsEntity toEntity(ImsDto dto);

    ImsDto toDto(ImsEntity entity);

    default List<ImsDto> toDto(List<ImsEntity> entities) {
        List<ImsDto> dtos = new ArrayList<>(entities.size());
        entities.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    default List<ImsEntity> toEntity(List<ImsDto> dtos) {
        List<ImsEntity> entities = new ArrayList<>(dtos.size());
        dtos.forEach(dto -> entities.add(toEntity(dto)));
        return entities;
    }
}

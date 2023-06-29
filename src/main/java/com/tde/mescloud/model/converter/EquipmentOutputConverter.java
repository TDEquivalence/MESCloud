package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;

import java.util.ArrayList;
import java.util.List;

public interface EquipmentOutputConverter {

    EquipmentOutputDto toDto(EquipmentOutputEntity entity);

    default List<EquipmentOutputDto> toDto(List<EquipmentOutputEntity> entities) {

        List<EquipmentOutputDto> dtos = new ArrayList<>(entities.size());
        for (EquipmentOutputEntity entity : entities) {
            EquipmentOutputDto equipmentOutputDto = toDto(entity);
            dtos.add(equipmentOutputDto);
        }

        return dtos;
    }

    default List<EquipmentOutputEntity> toEntity(List<EquipmentOutputDto> dtos) {

        List<EquipmentOutputEntity> entities = new ArrayList<>(dtos.size());
        for (EquipmentOutputDto dto : dtos) {
            EquipmentOutputEntity entity = toEntity(dto);
            entities.add(entity);
        }

        return entities;
    }

    EquipmentOutputEntity toEntity(EquipmentOutputDto dto);
}

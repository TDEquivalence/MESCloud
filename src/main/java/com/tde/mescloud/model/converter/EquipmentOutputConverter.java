package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;

import java.util.ArrayList;
import java.util.List;

public interface EquipmentOutputConverter {

    EquipmentOutput convertToDomainObject(EquipmentOutputEntity entity);

    default List<EquipmentOutput> convertToDomainObject(List<EquipmentOutputEntity> equipmentOutputEntities) {

        List<EquipmentOutput> equipmentOutputs = new ArrayList<>(equipmentOutputEntities.size());
        for (EquipmentOutputEntity equipmentOutputEntity : equipmentOutputEntities) {
            EquipmentOutput equipmentOutput = convertToDomainObject(equipmentOutputEntity);
            equipmentOutputs.add(equipmentOutput);
        }

        return equipmentOutputs;
    }

    EquipmentOutputDto convertToDto(EquipmentOutput equipmentOutput);

    default List<EquipmentOutputDto> convertToDto(List<EquipmentOutput> equipmentOutputs) {

        List<EquipmentOutputDto> equipmentOutputDtos = new ArrayList<>(equipmentOutputs.size());
        for (EquipmentOutput equipmentOutput : equipmentOutputs) {
            EquipmentOutputDto equipmentOutputDto = convertToDto(equipmentOutput);
            equipmentOutputDtos.add(equipmentOutputDto);
        }

        return equipmentOutputDtos;
    }

    EquipmentOutputDto toDto(EquipmentOutputEntity entity);

    default List<EquipmentOutputDto> toDto(List<EquipmentOutputEntity> entities) {

        List<EquipmentOutputDto> dtos = new ArrayList<>(entities.size());
        for (EquipmentOutputEntity entity : entities) {
            EquipmentOutputDto equipmentOutputDto = toDto(entity);
            dtos.add(equipmentOutputDto);
        }

        return dtos;
    }
}

package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;

import java.util.List;

public interface EquipmentOutputConverter {

    EquipmentOutputDto toDto(EquipmentOutputEntity entity);

    List<EquipmentOutputDto> toDto(List<EquipmentOutputEntity> equipmentOutputEntityList);

    EquipmentOutputEntity convertToEntity(EquipmentOutputDto equipmentOutputDto);

    List<EquipmentOutputEntity> convertToEntity(List<EquipmentOutputDto> equipmentOutputDtoList);
}

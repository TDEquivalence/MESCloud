package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentStatusRecordDto;
import com.tde.mescloud.model.entity.EquipmentStatusRecordEntity;

import java.util.List;

public interface EquipmentStatusRecordConverter {

    EquipmentStatusRecordDto toDto(EquipmentStatusRecordEntity entity);

    List<EquipmentStatusRecordDto> toDto(List<EquipmentStatusRecordEntity> equipmentOutputEntityList);

    EquipmentStatusRecordEntity toEntity(EquipmentStatusRecordDto equipmentOutputDto);

    List<EquipmentStatusRecordEntity> toEntity(List<EquipmentStatusRecordDto> equipmentOutputDtoList);
}

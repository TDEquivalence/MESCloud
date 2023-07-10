package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;

public interface CountingEquipmentConverter {

    CountingEquipmentDto convertToDto(CountingEquipmentEntity entity);

    CountingEquipmentEntity convertToEntity(CountingEquipmentDto dto);
}

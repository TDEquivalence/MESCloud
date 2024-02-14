package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;

public interface CountingEquipmentConverter {

    CountingEquipmentDto convertToDto(CountingEquipmentEntity entity);

    CountingEquipmentEntity convertToEntity(CountingEquipmentDto dto);

    CountingEquipmentEntity convertToEntity(RequestConfigurationDto dto);
}

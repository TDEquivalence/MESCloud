package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentOutputAliasDto;
import com.tde.mescloud.model.entity.EquipmentOutputAliasEntity;

public interface EquipmentOutputAliasConverter {

    EquipmentOutputAliasDto toDto(EquipmentOutputAliasEntity entity);

    EquipmentOutputAliasEntity toEntity(EquipmentOutputAliasDto dto);
}

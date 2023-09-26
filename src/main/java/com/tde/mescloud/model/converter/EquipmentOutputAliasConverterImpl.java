package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentOutputAliasDto;
import com.tde.mescloud.model.entity.EquipmentOutputAliasEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Log
@AllArgsConstructor
public class EquipmentOutputAliasConverterImpl implements EquipmentOutputAliasConverter {

    private ModelMapper modelMapper;

    @Override
    public EquipmentOutputAliasDto toDto(EquipmentOutputAliasEntity entity) {
        return modelMapper.map(entity, EquipmentOutputAliasDto.class);
    }

    @Override
    public EquipmentOutputAliasEntity toEntity(EquipmentOutputAliasDto dto) {
        return modelMapper.map(dto, EquipmentOutputAliasEntity.class);
    }
}

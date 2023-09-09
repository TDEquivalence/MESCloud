package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Log
@AllArgsConstructor
public class EquipmentOutputConverterImpl implements EquipmentOutputConverter {

    private ModelMapper modelMapper;
    private EquipmentOutputAliasConverter equipmentOutputAliasConverter;

    @Override
    public EquipmentOutputDto toDto(EquipmentOutputEntity entity) {
        return modelMapper.map(entity, EquipmentOutputDto.class);
    }

    /*
     EquipmentOutputDto equipmentOutputDto = new EquipmentOutputDto();
        EquipmentOutputDto equipmentOutputDto = new EquipmentOutputDto();
        equipmentOutputDto.setId(entity.getId());
        equipmentOutputDto.setCode(entity.getCode());
        equipmentOutputDto.setValidForProduction(entity.isValidForProduction());

        if (entity.getAlias() != null) {
            equipmentOutputDto.setAlias(entity.getAlias().getAlias());
        }

        return equipmentOutputDto;
     */
}

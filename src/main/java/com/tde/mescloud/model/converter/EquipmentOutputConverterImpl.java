package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class EquipmentOutputConverterImpl implements EquipmentOutputConverter {

    public EquipmentOutput convertToDomainObject(EquipmentOutputEntity entity) {

        EquipmentOutput equipmentOutput = new EquipmentOutput();
        equipmentOutput.setId(entity.getId().intValue());
        equipmentOutput.setCode(entity.getCode());

        //TODO: Alias within alias is a poor naming choice
        if (entity.getAlias() != null) {
            equipmentOutput.setAlias(entity.getAlias().getAlias());
        }

        return equipmentOutput;
    }

    @Override
    public EquipmentOutputDto convertToDto(EquipmentOutput equipmentOutput) {

        EquipmentOutputDto equipmentOutputDto = new EquipmentOutputDto();
        equipmentOutputDto.setId(equipmentOutput.getId());
        equipmentOutputDto.setCode(equipmentOutput.getCode());
        equipmentOutputDto.setAlias(equipmentOutput.getAlias());

        //TODO: Implement
        CountingEquipmentDto countingEquipmentDto = null;

        return equipmentOutputDto;
    }

    @Override
    public EquipmentOutputDto toDto(EquipmentOutputEntity entity) {

        EquipmentOutputDto equipmentOutputDto = new EquipmentOutputDto();
        equipmentOutputDto.setId(entity.getId());
        equipmentOutputDto.setCode(entity.getCode());

        if (entity.getAlias() != null) {
            equipmentOutputDto.setAlias(entity.getAlias().getAlias());
        }

        return equipmentOutputDto;
    }
}

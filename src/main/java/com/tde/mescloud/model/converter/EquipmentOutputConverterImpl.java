package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class EquipmentOutputConverterImpl implements EquipmentOutputConverter{

    public EquipmentOutput convertToDO(EquipmentOutputEntity entity) {

        if (entity == null) {
            String msg = "Null EquipmentOutputEntity provided for Domain Object conversion";
            log.warning(msg);
            throw new IllegalArgumentException(msg);
        }

        EquipmentOutput equipmentOutput = new EquipmentOutput();
        equipmentOutput.setId(entity.getId().intValue());
        //TODO: set counting equipment
        //TODO: Alias within alias is a poor naming choice
        if (entity.getAlias() != null) {
            equipmentOutput.setAlias(entity.getAlias().getAlias());
        } else {
            log.warning("No equipment output alias found during conversion to Domain Object.");
        }
        equipmentOutput.setCode(entity.getCode());
        return equipmentOutput;
    }
}

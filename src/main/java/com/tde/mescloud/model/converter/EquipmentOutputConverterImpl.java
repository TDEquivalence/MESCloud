package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class EquipmentOutputConverterImpl implements EquipmentOutputConverter{

    public EquipmentOutput convertToDomainObject(EquipmentOutputEntity entity) {

        EquipmentOutput equipmentOutput = new EquipmentOutput();
        equipmentOutput.setId(entity.getId().intValue());
        equipmentOutput.setCode(entity.getCode());

        //TODO: remove to converter
        CountingEquipment countingEquipment = new CountingEquipment(entity.getCountingEquipment());
        equipmentOutput.setCountingEquipment(countingEquipment);

        //TODO: Alias within alias is a poor naming choice
        if (entity.getAlias() != null) {
            equipmentOutput.setAlias(entity.getAlias().getAlias());
        }

        return equipmentOutput;
    }
}

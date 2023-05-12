package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;

public interface EquipmentOutputConverter {
    EquipmentOutput convertToDO(EquipmentOutputEntity entity);
}

package com.tde.mescloud.service;

import com.tde.mescloud.model.EquipmentOutput;

public interface EquipmentOutputService {

    EquipmentOutput findByCode(String equipmentOutputCode);
}

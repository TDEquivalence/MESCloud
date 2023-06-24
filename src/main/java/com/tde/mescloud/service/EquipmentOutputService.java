package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.EquipmentOutputDto;

public interface EquipmentOutputService {

    EquipmentOutputDto findByCode(String equipmentOutputCode);
}

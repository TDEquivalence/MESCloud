package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.EquipmentOutputDto;

import java.util.Optional;

public interface EquipmentOutputService {

    Optional<EquipmentOutputDto> findByCode(String equipmentOutputCode);
}

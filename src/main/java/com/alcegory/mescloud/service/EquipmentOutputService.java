package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.EquipmentOutputDto;

import java.util.Optional;

public interface EquipmentOutputService {

    Optional<EquipmentOutputDto> findByCode(String equipmentOutputCode);
}

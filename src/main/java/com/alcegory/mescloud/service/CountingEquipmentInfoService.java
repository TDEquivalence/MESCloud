package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CountingEquipmentInfoDto;

import java.util.Optional;

public interface CountingEquipmentInfoService {
    
    Optional<CountingEquipmentInfoDto> findEquipmentWithProductionOrderById(long id);
}

package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.EquipmentOutputDto;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;

import java.util.List;
import java.util.Optional;

public interface EquipmentOutputService {

    Optional<EquipmentOutputDto> findByCode(String equipmentOutputCode);

    EquipmentOutputDto save(EquipmentOutputEntity entity);

    List<EquipmentOutputEntity> saveAll(List<EquipmentOutputEntity> equipmentOutputToUpdate);
}

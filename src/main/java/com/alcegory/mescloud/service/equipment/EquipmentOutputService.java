package com.alcegory.mescloud.service.equipment;

import com.alcegory.mescloud.model.dto.EquipmentOutputDto;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;

import java.util.List;
import java.util.Optional;

public interface EquipmentOutputService {

    Optional<EquipmentOutputDto> findByCode(String equipmentOutputCode);

    EquipmentOutputDto save(EquipmentOutputEntity entity);

    List<EquipmentOutputEntity> saveAll(List<EquipmentOutputEntity> equipmentOutputToUpdate);

    List<Long> findIdsByCountingEquipmentId(Long equipmentId);

    Long findIdByCountingEquipmentId(Long equipmentId);
}

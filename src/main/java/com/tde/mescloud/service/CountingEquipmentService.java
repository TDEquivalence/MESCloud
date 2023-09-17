package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.RequestConfigurationDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentService {

    List<CountingEquipmentDto> findAllWithLastProductionOrder();

    Optional<CountingEquipmentDto> findById(long id);

    Optional<CountingEquipmentDto> findByCode(String code);

    CountingEquipmentDto save(CountingEquipmentEntity countingEquipment);

    CountingEquipmentDto save(CountingEquipmentDto countingEquipment);

    Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus);

    Optional<CountingEquipmentDto> updateIms(Long equipmentId, Long imsId);

    CountingEquipmentDto setConfiguration(RequestConfigurationDto request);
}

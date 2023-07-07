package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CountingEquipmentDto;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentService {

    List<CountingEquipmentDto> findAllWithLastProductionOrder();

    Optional<CountingEquipmentDto> findById(long id);

    Optional<CountingEquipmentDto> findByCode(String code);

    CountingEquipmentDto save(CountingEquipmentDto countingEquipment);
}

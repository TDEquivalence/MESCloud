package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CountingEquipmentDto;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentService {

    List<CountingEquipmentDto> findAll();

    Optional<CountingEquipmentDto> findById(long id);
}

package com.tde.mescloud.service;

import com.tde.mescloud.model.CountingEquipment;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentService {

    List<CountingEquipment> findAll();

    Optional<CountingEquipment> findById(long id);
}

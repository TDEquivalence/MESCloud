package com.alcegory.mescloud.service;

import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.RequestConfigurationDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import org.springframework.dao.EmptyResultDataAccessException;

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

    CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request)
            throws IncompleteConfigurationException, EmptyResultDataAccessException, ActiveProductionOrderException;
}

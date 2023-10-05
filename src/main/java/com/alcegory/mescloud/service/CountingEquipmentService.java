package com.alcegory.mescloud.service;

import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.EquipmentNotFoundException;
import com.alcegory.mescloud.exception.ImsNotFoundException;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.RequestConfigurationDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface CountingEquipmentService {

    List<CountingEquipmentDto> findAllWithLastProductionOrder();

    Optional<CountingEquipmentDto> findById(long id);

    Optional<CountingEquipmentDto> findByCode(String code);

    CountingEquipmentDto save(CountingEquipmentEntity countingEquipment);

    CountingEquipmentDto save(CountingEquipmentDto countingEquipment);

    Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus);

    CountingEquipmentDto updateIms(Long equipmentId, Long imsId)
            throws EquipmentNotFoundException, ImsNotFoundException, IllegalStateException;

    CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request)
            throws IncompleteConfigurationException, EmptyResultDataAccessException, ActiveProductionOrderException;
}

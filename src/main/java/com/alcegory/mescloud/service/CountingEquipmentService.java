package com.alcegory.mescloud.service;

import com.alcegory.mescloud.exception.*;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
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

    CountingEquipmentDto updateIms(Long equipmentId, Long imsId)
            throws EquipmentNotFoundException, ImsNotFoundException, IllegalStateException;

    CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request)
            throws IncompleteConfigurationException, EmptyResultDataAccessException, ActiveProductionOrderException, MesMqttException;

    CountingEquipmentDto setOperationStatus(CountingEquipmentEntity countingEquipment, CountingEquipmentEntity.OperationStatus status);

    void setOperationStatusByCode(String equipmentCode, CountingEquipmentEntity.OperationStatus idle);

    List<Long> findAllIds();

    Long findIdByAlias(String alias);

    Optional<CountingEquipmentDto> findEquipmentWithProductionOrderById(long id);

    boolean hasActiveProductionOrder(Long equipmentId);
}

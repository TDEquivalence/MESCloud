package com.alcegory.mescloud.service;

import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface CountingEquipmentManagementService {

    Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus);

    CountingEquipmentDto updateIms(Long equipmentId, Long imsId, Authentication authentication);

    CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request, Authentication authentication) throws MesMqttException;

    void setOperationStatusByCode(String equipmentCode, CountingEquipmentEntity.OperationStatus status);
}

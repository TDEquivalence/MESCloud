package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.TemplateDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface CountingEquipmentManagementService {

    Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus);

    CountingEquipmentDto updateIms(Long equipmentId, Long imsId, Authentication authentication);

    CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request, Authentication authentication) throws MesMqttException;

    void setOperationStatusByCode(String equipmentCode, CountingEquipmentEntity.OperationStatus status);

    TemplateDto findEquipmentTemplate(long id);
}

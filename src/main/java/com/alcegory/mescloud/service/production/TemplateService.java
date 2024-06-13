package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.dto.equipment.TemplateDto;
import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;

import java.util.Optional;

public interface TemplateService {

    TemplateEntity getTemplateWithFields();

    Optional<TemplateDto> findTemplateByCountingEquipmentId(Long countingEquipmentId);
}

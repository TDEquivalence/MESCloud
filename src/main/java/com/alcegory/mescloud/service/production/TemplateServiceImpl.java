package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.equipment.TemplateDto;
import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;
import com.alcegory.mescloud.repository.production.ProductionOrderTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final ProductionOrderTemplateRepository templateRepository;
    private final GenericConverter<TemplateEntity, TemplateDto> templateConverter;

    @Override
    public TemplateEntity getTemplateWithFields() {
        Optional<TemplateEntity> optionalTemplate = templateRepository.findById(1L);

        return optionalTemplate.orElseGet(TemplateEntity::new);
    }

    @Transactional(readOnly = true)
    public Optional<TemplateDto> findTemplateByCountingEquipmentId(Long countingEquipmentId) {
        return templateRepository.findByCountingEquipmentId(countingEquipmentId)
                .map(template -> templateConverter.toDto(template, TemplateDto.class));
    }

}

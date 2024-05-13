package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.entity.ProductionOrderTemplateEntity;
import com.alcegory.mescloud.repository.production.ProductionOrderTemplateRepository;
import com.alcegory.mescloud.repository.production.TemplateFieldMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final ProductionOrderTemplateRepository templateRepository;
    private final TemplateFieldMappingRepository templateFieldMappingRepository;

    @Override
    public ProductionOrderTemplateEntity getTemplateWithFields() {
        // Retrieve the ProductionOrderTemplateEntity by its ID
        Optional<ProductionOrderTemplateEntity> optionalTemplate = templateRepository.findById(1L);

        // If the template is found, fetch its fields
        if (optionalTemplate.isPresent()) {
            ProductionOrderTemplateEntity template = optionalTemplate.get();
            template.getFields().size(); // Force eager fetching of fields
            return template;
        }

        return new ProductionOrderTemplateEntity();
    }

}

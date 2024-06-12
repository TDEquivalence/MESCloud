package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;
import com.alcegory.mescloud.repository.production.ProductionOrderTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
import org.springframework.transaction.annotation.Transactional;
=======
>>>>>>> test_environment

import java.util.Optional;

@Service
@AllArgsConstructor
<<<<<<< HEAD
@Transactional
=======
>>>>>>> test_environment
public class TemplateServiceImpl implements TemplateService {

    private final ProductionOrderTemplateRepository templateRepository;

    @Override
    public TemplateEntity getTemplateWithFields() {
        Optional<TemplateEntity> optionalTemplate = templateRepository.findById(1L);

        return optionalTemplate.orElseGet(TemplateEntity::new);
    }

}

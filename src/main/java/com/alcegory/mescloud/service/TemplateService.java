package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.entity.ProductionOrderTemplateEntity;

public interface TemplateService {

    ProductionOrderTemplateEntity getTemplateWithFields(Long templateId);
}

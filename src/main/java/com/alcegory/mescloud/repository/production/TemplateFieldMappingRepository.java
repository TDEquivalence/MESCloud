package com.alcegory.mescloud.repository.production;

import com.alcegory.mescloud.model.entity.equipment.TemplateFieldMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateFieldMappingRepository extends JpaRepository<TemplateFieldMappingEntity, Long> {

}

package com.alcegory.mescloud.repository.production;

import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderTemplateRepository extends JpaRepository<TemplateEntity, Long> {

}

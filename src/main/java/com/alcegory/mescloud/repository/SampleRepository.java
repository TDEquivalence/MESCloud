package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {

    SampleEntity findByComposedProductionOrderId(Long composedProductionOrderId);
}

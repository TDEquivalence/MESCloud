package com.alcegory.mescloud.repository.composed;

import com.alcegory.mescloud.model.entity.composed.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {

    SampleEntity findByComposedProductionOrderId(Long composedProductionOrderId);
}

package com.alcegory.mescloud.repository.composed;

import com.alcegory.mescloud.model.entity.composed.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, Long> {

    BatchEntity findByComposedProductionOrderId(Long composedProductionOrderId);
}

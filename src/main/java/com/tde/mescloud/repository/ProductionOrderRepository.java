package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ProductionOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    ProductionOrderEntity findByCode(String equipmentOutputCode);

    ProductionOrderEntity findTopByOrderByIdDesc();
}

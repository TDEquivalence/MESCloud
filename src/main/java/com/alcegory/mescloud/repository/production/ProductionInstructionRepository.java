package com.alcegory.mescloud.repository.production;

import com.alcegory.mescloud.model.entity.production.ProductionInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionInstructionRepository extends JpaRepository<ProductionInstructionEntity, Long> {

    List<ProductionInstructionEntity> findByProductionOrderId(Long productionOrderId);
}
package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ProductionInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionInstructionRepository extends JpaRepository<ProductionInstructionEntity, Long> {
}
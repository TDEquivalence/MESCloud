package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ProductionOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    Optional<ProductionOrderEntity> findByCode(String equipmentOutputCode);

    ProductionOrderEntity findTopByOrderByIdDesc();

    @Query(value = "SELECT * FROM production_order po WHERE (po.equipment_id = :equipmentId AND po.is_completed = false) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActive(long equipmentId);
}

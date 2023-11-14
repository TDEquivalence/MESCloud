package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    Optional<ProductionOrderEntity> findByCode(String equipmentOutputCode);

    Optional<ProductionOrderEntity> findTopByOrderByIdDesc();

    List<ProductionOrderEntity> findByIdIn(List<Long> ids);

    List<ProductionOrderSummaryEntity> findCompletedWithoutComposed();

    List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedProductionOrderId);

    @Query("SELECT p FROM production_order p " +
            "WHERE (p.completedAt > :startDate OR p.completedAt IS NULL) " +
            "AND p.createdAt < :endDate " +
            "AND (p.equipment.id = :equipmentId)")
    List<ProductionOrderEntity> findByEquipmentAndPeriod(Long equipmentId, Date startDate, Date endDate);

    @Query("SELECT po.isCompleted FROM production_order po WHERE po.code = :productionOrderCode")
    boolean isCompleted(String productionOrderCode);
}

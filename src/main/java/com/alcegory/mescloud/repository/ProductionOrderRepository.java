package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    Optional<ProductionOrderEntity> findByCode(String equipmentOutputCode);

    Optional<ProductionOrderEntity> findTopByOrderByIdDesc();

    @Query(value = "SELECT * FROM production_order po WHERE (po.equipment_id = :equipmentId AND po.is_completed = false) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActive(long equipmentId);

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

    @Query(value = "SELECT cr.registered_at FROM counter_record cr " +
            "WHERE cr.production_order_id = :productionOrderId " +
            "ORDER BY cr.registered_at DESC LIMIT 1", nativeQuery = true)
    Date findLastCounterRecordDateByProductionOrderId(@Param("productionOrderId") Long productionOrderId);
}

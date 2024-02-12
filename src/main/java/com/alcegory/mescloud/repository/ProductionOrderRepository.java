package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    Optional<ProductionOrderEntity> findByCode(String equipmentOutputCode);

    Optional<ProductionOrderEntity> findTopByOrderByIdDesc();

    @Query(value = "SELECT * FROM production_order po WHERE (po.equipment_id = :equipmentId AND po.is_completed = false) " +
            "ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActive(long equipmentId);

    List<ProductionOrderEntity> findByIdIn(List<Long> ids);

    List<ProductionOrderSummaryEntity> findCompletedWithoutComposed(Timestamp startDate, Timestamp endDate);

    List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedProductionOrderId);

    @Query(value = "SELECT * FROM production_order p " +
            "WHERE (p.completed_at > :startDate OR p.completed_at IS NULL) " +
            "AND p.created_at < :endDate " +
            "AND (p.equipment_id = :equipmentId) " +
            "AND (:productionOrderCode IS NULL OR p.code = :productionOrderCode)", nativeQuery = true)
    List<ProductionOrderEntity> findByEquipmentAndPeriod(
            @Param("equipmentId") Long equipmentId,
            @Param("productionOrderCode") String productionOrderCode,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT po.isCompleted FROM production_order po WHERE po.code = :productionOrderCode")
    boolean isCompleted(String productionOrderCode);

    boolean existsByEquipmentIdAndIsCompletedFalse(long equipmentId);

    List<ProductionOrderSummaryEntity> findProductionOrderSummaryByComposedId(Long composedProductionOrderId);
}

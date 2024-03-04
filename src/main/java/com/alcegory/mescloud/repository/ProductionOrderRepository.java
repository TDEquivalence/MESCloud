package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    Optional<ProductionOrderEntity> findActiveByEquipmentId(long equipmentId);

    @Query(value = "SELECT * FROM production_order po WHERE po.equipment_id = :equipmentId " +
            "ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findLastByEquipmentId(long equipmentId);

    @Query(value = "SELECT po.* FROM production_order po " +
            "JOIN counting_equipment ce ON po.equipment_id = ce.id " +
            "WHERE (ce.code = :equipmentCode AND po.is_completed = false) " +
            "ORDER BY po.id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActiveByEquipmentCode(@Param("equipmentCode") String equipmentCode);

    List<ProductionOrderEntity> findByIdIn(List<Long> ids);

    List<ProductionOrderSummaryEntity> findCompleted(Timestamp startDate, Timestamp endDate, boolean withoutComposed);

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

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM production_order po " +
            "WHERE po.equipment_id = :equipmentId " +
            "AND po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment_id = :equipmentId) " +
            "AND po.is_completed = false",
            nativeQuery = true)
    boolean hasEquipmentActiveProductionOrder(@Param("equipmentId") Long equipmentId);

    @Query(value = "SELECT COUNT(*) > 0 " +
            "FROM production_order po " +
            "JOIN counting_equipment ce ON po.equipment_id = ce.id " +
            "WHERE ce.code = :countingEquipmentCode " +
            "AND po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment_id = ce.id) " +
            "AND po.is_completed = false", nativeQuery = true)
    boolean hasEquipmentActiveProductionOrder(@Param("countingEquipmentCode") String countingEquipmentCode);

    List<ProductionOrderSummaryEntity> findProductionOrderSummaryByComposedId(Long composedProductionOrderId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM production_order WHERE code = :productionOrderCode", nativeQuery = true)
    void deleteByCode(String productionOrderCode);
}

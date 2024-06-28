package com.alcegory.mescloud.repository.production;

import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrderEntity, Long> {

    Optional<ProductionOrderEntity> findByCode(String productionOrderCode);

    Optional<ProductionOrderEntity> findTopByOrderByIdDesc();

    @Query("SELECT po FROM production_order po WHERE po.code LIKE :sectionPrefix || :codePrefix ORDER BY po.id DESC")
    Optional<ProductionOrderEntity> findTopBySectionPrefixOrderByIdDesc(@Param("sectionPrefix") String sectionPrefix, @Param("codePrefix") String codePrefix);

    @Query(value = "SELECT * FROM production_order po WHERE (po.equipment_id = :equipmentId AND po.is_completed = false) " +
            "ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActiveByEquipmentId(long equipmentId);

    Optional<ProductionOrderEntity> findLastByEquipmentId(long equipmentId);

    @Query(value = "SELECT po.* FROM production_order po " +
            "JOIN counting_equipment ce ON po.equipment_id = ce.id " +
            "WHERE (ce.code = :equipmentCode AND po.is_completed = false) " +
            "ORDER BY po.id DESC LIMIT 1", nativeQuery = true)
    Optional<ProductionOrderEntity> findActiveByEquipmentCode(@Param("equipmentCode") String equipmentCode);

    List<ProductionOrderEntity> findByIdIn(List<Long> ids);

    List<ProductionOrderEntity> findCompleted(long sectionId, boolean withoutComposed, Filter filter, Timestamp startDate, Timestamp endDate);

    List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedProductionOrderId);

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

    List<ProductionOrderEntity> findProductionOrderSummaryByComposedId(Long composedProductionOrderId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM production_order WHERE code = :productionOrderCode", nativeQuery = true)
    void deleteByCode(String productionOrderCode);

    @Query(value = "SELECT composed_production_order_id FROM production_order WHERE code = :code", nativeQuery = true)
    Long findComposedProductionOrderIdByCode(@Param("code") String code);
}

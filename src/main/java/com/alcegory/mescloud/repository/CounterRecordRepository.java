package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.dto.FilterDto;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import com.alcegory.mescloud.model.filter.Filter;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface CounterRecordRepository extends CrudRepository<CounterRecordEntity, Long> {

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId AND cr.equipment_output_id = :equipmentOutputId) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<CounterRecordEntity> findLastByProductionOrderId(Long productionOrderId, Long equipmentOutputId);

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId) LIMIT 1", nativeQuery = true)
    Optional<CounterRecordEntity> findLastByProductionOrderId(Long productionOrderId);

    List<CounterRecordConclusionEntity> findLastPerProductionOrder(Filter filterDto);

    List<CounterRecordConclusionEntity> findLastPerProductionOrder(FilterDto filterDto);


    List<CounterRecordEntity> findLastPerProductionOrderAndEquipmentOutputPerDay(FilterDto filterDto);

    List<CounterRecordEntity> getFilteredAndPaginated(Filter filterDto);

    Integer sumValidCounterIncrement(Long countingEquipmentId, FilterDto filter);

    Integer sumValidCounterIncrementForApprovedPO(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    Integer sumCounterIncrement(Long countingEquipmentId, FilterDto filter);

    @Query(value = "SELECT " +
            "SUM(cr.increment_active_time) - COALESCE((" +
            "    SELECT cr_sub.increment_active_time " +
            "    FROM counter_record cr_sub " +
            "    WHERE cr_sub.production_order_id = :productionOrderId " +
            "        AND cr_sub.equipment_output_id = :equipmentOutputId " +
            "        AND cr_sub.registered_at BETWEEN :startDate AND :endDate " +
            "    ORDER BY cr_sub.registered_at " +
            "    LIMIT 1" +
            "), 0) AS sum_increment_active_time " +
            "FROM counter_record cr " +
            "WHERE " +
            "    cr.production_order_id = :productionOrderId " +
            "    AND cr.equipment_output_id = :equipmentOutputId " +
            "    AND cr.registered_at BETWEEN :startDate AND :endDate " +
            "GROUP BY cr.production_order_id", nativeQuery = true)
    Integer sumIncrementActiveTimeByProductionOrderId(
            @Param("productionOrderId") Long productionOrderId,
            @Param("equipmentOutputId") Long equipmentOutputId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);

    @Query(value = "SELECT cr.registered_at " +
            "FROM counter_record cr " +
            "WHERE cr.production_order_id = :productionOrderId " +
            "ORDER BY cr.registered_at DESC LIMIT 1", nativeQuery = true)
    Timestamp findLatestRegisteredAtByProductionOrderId(@Param("productionOrderId") Long productionOrderId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM counter_record cr " +
            "WHERE cr.production_order_id IN " +
            "(SELECT po.id FROM production_order po WHERE po.production_order_code = :productionOrderCode)", nativeQuery = true)
    void deleteByProductionOrderCode(String productionOrderCode);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM counter_record cr " +
            "JOIN production_order po ON cr.production_order_id = po.id " +
            "WHERE po.code = :productionOrderCode " +
            "AND (cr.increment IS NOT NULL AND cr.increment > 0)", nativeQuery = true)
    boolean hasIncrementByProductionOrderCode(String productionOrderCode);
}
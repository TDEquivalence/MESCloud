package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;
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

    List<CounterRecordConclusionEntity> findLastPerProductionOrder(CounterRecordFilter filterDto);

    List<CounterRecordConclusionEntity> findLastPerProductionOrder(KpiFilterDto filterDto);


    List<CounterRecordEntity> findLastPerProductionOrderAndEquipmentOutputPerDay(KpiFilterDto filterDto);


    //    @EntityGraph(attributePaths = { "equipmentOutput", "equipmentOutput.countingEquipment", "productionOrder" })
    List<CounterRecordEntity> getFilteredAndPaginated(CounterRecordFilter filterDto);

    Integer sumValidCounterIncrement(Long countingEquipmentId, KpiFilterDto filter);

    Integer sumValidCounterIncrementForApprovedPO(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    Integer sumCounterIncrement(Long countingEquipmentId, KpiFilterDto filter);

    @Query(value = "SELECT SUM(cr.increment_active_time) AS sum_increment_active_time " +
            "FROM counter_record cr " +
            "WHERE cr.production_order_id = :productionOrderId " +
            "AND cr.equipment_output_id = :equipmentOutputId " +
            "AND cr.registered_at BETWEEN :startDate AND :endDate " +
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
}
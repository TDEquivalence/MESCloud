package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilter;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.entity.CounterRecordConclusionEntity;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CounterRecordRepository extends CrudRepository<CounterRecordEntity, Long> {

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId AND cr.equipment_output_id = :equipmentOutputId) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<CounterRecordEntity> findLastByProductionOrderId(Long productionOrderId, Long equipmentOutputId);

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId) LIMIT 1", nativeQuery = true)
    Optional<CounterRecordEntity> findLastByProductionOrderId(Long productionOrderId);

    List<CounterRecordConclusionEntity> findLastPerProductionOrder(CounterRecordFilter filterDto);

    List<CounterRecordConclusionEntity> findLastPerProductionOrder(KpiFilterDto filterDto);

    //    @EntityGraph(attributePaths = { "equipmentOutput", "equipmentOutput.countingEquipment", "productionOrder" })
    List<CounterRecordEntity> getFilteredAndPaginated(CounterRecordFilter filterDto);

    /*@Query(value = "SELECT SUM(total_increment) AS total_increment " +
            "FROM ( " +
            "    SELECT " +
            "        cr.production_order_id, " +
            "        SUM(cr.increment) AS total_increment " +
            "    FROM " +
            "        equipment_output eo " +
            "    JOIN " +
            "        counter_record cr ON eo.id = cr.equipment_output_id " +
            "    WHERE " +
            "        eo.is_valid_for_production = true " +
            "        AND eo.counting_equipment_id = :countingEquipmentId " + // Use the parameter here
            "        AND cr.production_order_id IS NOT NULL " +
            "    GROUP BY " +
            "        cr.production_order_id " +
            ") AS subquery", nativeQuery = true)*/
    Integer calculateIncrement(Long countingEquipmentId);

    /*@Query(value = "SELECT SUM(total_increment) AS total_increment " +
            "FROM ( " +
            "    SELECT " +
            "        cr.production_order_id, " +
            "        SUM(cr.increment) AS total_increment " +
            "    FROM " +
            "        equipment_output eo " +
            "    JOIN " +
            "        counter_record cr ON eo.id = cr.equipment_output_id " +
            "    JOIN " +
            "        production_order po ON cr.production_order_id = po.id " +
            "    WHERE " +
            "        eo.is_valid_for_production = true " +
            "        AND eo.counting_equipment_id = :countingEquipmentId " + // Use the parameter here
            "        AND cr.production_order_id IS NOT NULL " +
            "        AND po.is_approved = true " +  // Include this condition
            "    GROUP BY " +
            "        cr.production_order_id " +
            ") AS subquery", nativeQuery = true)*/
    Integer calculateIncrementWithApprovedPO(@Param("countingEquipmentId") Long countingEquipmentId);
}
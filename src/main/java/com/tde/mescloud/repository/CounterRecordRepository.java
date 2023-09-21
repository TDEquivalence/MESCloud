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

    @Query(nativeQuery = true, value =
            "SELECT cr.*," +
                    "       cr.production_order_id AS production_order_id_alias," +
                    "       cr.equipment_output_id AS equipment_output_id_alias " +
                    "FROM counter_record cr " +
                    "INNER JOIN equipment_output eo ON cr.equipment_output_id = eo.id " +
                    "INNER JOIN counting_equipment ce ON eo.counting_equipment_id = ce.id " +
                    "WHERE ce.id = :countingEquipmentId " +
                    "  AND cr.is_valid_for_production = true " +
                    "  AND (cr.equipment_output_id, cr.production_order_id, cr.computed_value, cr.id) IN (" +
                    "    SELECT equipment_output_id," +
                    "           production_order_id," +
                    "           MAX(computed_value)," +
                    "           MAX(id) " +
                    "    FROM counter_record " +
                    "    WHERE ce.id = :countingEquipmentId " +
                    "      AND is_valid_for_production = true " +
                    "    GROUP BY equipment_output_id, production_order_id" +
                    ")")
    List<CounterRecordEntity> getAllMaxValidCounterRecordByEquipmentId(@Param("countingEquipmentId") Long countingEquipmentId);


    @Query(nativeQuery = true, value =
            "SELECT SUM(cr.computed_value) AS total_computed_value " +
                    "FROM counter_record cr " +
                    "INNER JOIN equipment_output eo ON cr.equipment_output_id = eo.id " +
                    "INNER JOIN counting_equipment ce ON eo.counting_equipment_id = ce.id " +
                    "WHERE eo.is_valid_for_production = true " +
                    "AND eo.counting_equipment_id = :countingEquipmentId " +
                    "AND cr.is_valid_for_production = true " +
                    "AND (cr.production_order_id, cr.equipment_output_id, cr.computed_value) IN ( " +
                    "    SELECT cr2.production_order_id, cr2.equipment_output_id, MAX(cr2.computed_value) " +
                    "    FROM counter_record cr2 " +
                    "    WHERE cr2.equipment_output_id = eo.id " +
                    "    GROUP BY cr2.production_order_id, cr2.equipment_output_id" +
                    ")")
    Integer getCounterRecordsComputedValueSum(@Param("countingEquipmentId") Long countingEquipmentId);
}
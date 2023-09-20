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

    @Query(
            value = "SELECT cr.*, cr.production_order_id AS production_order_id_alias, cr.equipment_output_id AS equipment_output_id_alias " +
                    "FROM counter_record cr " +
                    "INNER JOIN equipment_output eo ON cr.equipment_output_id = eo.id " +
                    "WHERE eo.counting_equipment_id = :countingEquipmentId " +
                    "AND cr.is_valid_for_production = true " +
                    "AND cr.computed_value = ( " +
                    "    SELECT MAX(cr2.computed_value) " +
                    "    FROM counter_record cr2 " +
                    "    WHERE cr2.equipment_output_id = eo.id " +
                    ")",
            nativeQuery = true
    )
    List<CounterRecordEntity> getAllMaxValidCounterRecordByEquipmentId(@Param("countingEquipmentId") Long countingEquipmentId);

    @Query(nativeQuery = true, value =
            "SELECT SUM(cr.computed_value) AS total_computed_value " +
                    "FROM counter_record cr " +
                    "INNER JOIN equipment_output eo ON cr.equipment_output_id = eo.id " +
                    "WHERE eo.counting_equipment_id = :countingEquipmentId " +
                    "AND cr.is_valid_for_production = true " +
                    "AND cr.computed_value = ( " +
                    "    SELECT MAX(cr2.computed_value) " +
                    "    FROM counter_record cr2 " +
                    "    WHERE cr2.equipment_output_id = eo.id " +
                    ")")
    Integer getCounterRecordsComputedValueSum(@Param("countingEquipmentId") Long countingEquipmentId);
}
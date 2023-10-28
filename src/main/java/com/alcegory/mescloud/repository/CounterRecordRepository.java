package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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

    //    @EntityGraph(attributePaths = { "equipmentOutput", "equipmentOutput.countingEquipment", "productionOrder" })
    List<CounterRecordEntity> getFilteredAndPaginated(CounterRecordFilter filterDto);

    Integer sumValidCounterIncrement(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    Integer sumValidCounterIncrementForApprovedPO(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    Integer sumCounterIncrement(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    @Query("SELECT cr FROM CounterRecordEntity cr " +
            "WHERE (cr.equipmentOutput, cr.realValue, cr.computedValue, cr.increment, cr.productionOrder, cr.isValidForProduction) IN " +
            "(SELECT cr2.equipmentOutput, cr2.realValue, cr2.computedValue, cr2.increment, cr2.productionOrder, cr2.isValidForProduction " +
            "FROM CounterRecordEntity cr2 " +
            "GROUP BY cr2.equipmentOutput, cr2.realValue, cr2.computedValue, cr2.increment, cr2.productionOrder, cr2.isValidForProduction " +
            "HAVING COUNT(cr2) = 1)")
    List<CounterRecordEntity> checkIfRepeatedCounterRecords(List<CounterRecordEntity> counterRecords);
}
package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Date;
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
            "WHERE DATE(cr.registeredAt) = DATE(:rangeDateToCompare) " +
            "AND cr.equipmentOutput = :equipmentOutput " +
            "AND cr.realValue = :realValue " +
            "AND cr.computedValue = :computedValue " +
            "AND cr.increment = :increment " +
            "AND cr.productionOrder = :productionOrder")
    List<CounterRecordEntity> checkIfNonRepeatedCounterRecords(
            @Param("rangeDateToCompare") Date rangeDateToCompare,
            @Param("equipmentOutput") EquipmentOutputEntity equipmentOutput,
            @Param("realValue") double realValue,
            @Param("computedValue") double computedValue,
            @Param("increment") double increment,
            @Param("productionOrder") ProductionOrderEntity productionOrder
    );

}
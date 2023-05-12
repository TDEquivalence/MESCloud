package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.CounterRecordEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CounterRecordRepository extends CrudRepository<CounterRecordEntity, Long> {

    List<CounterRecordEntity> findByProductionOrderId(Long productionOrderId);

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId AND cr.equipment_output_id = :equipmentOutputId) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    CounterRecordEntity findLast(Long productionOrderId, Long equipmentOutputId);
}

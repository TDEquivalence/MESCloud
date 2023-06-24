package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountingEquipmentRepository extends CrudRepository<CountingEquipmentEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "outputs")
    List<CountingEquipmentEntity> findAll();

    @Query(value = "SELECT DISTINCT " +
            "ce.id AS id, " +
            "ce.code AS code, " +
            "ce.alias AS alias, " +
            "ce.section_id AS section, " +
            "ce.equipment_status AS equipmentStatus, " +
            "ce.p_timer_communication_cycle AS pTimerCommunicationCycle, " +
            "CASE WHEN po.is_completed = false THEN true ELSE false END AS hasActiveProductionOrder " +
            "FROM counting_equipment ce " +
            "LEFT JOIN production_order po ON ce.id = po.equipment_id " +
            "LEFT JOIN equipment_output eo ON eo.counting_equipment_id = ce.id " +
            "ORDER BY ce.id", nativeQuery = true)
    List<CountingEquipmentProjection> findAllWithProductionOrderStatus();
}

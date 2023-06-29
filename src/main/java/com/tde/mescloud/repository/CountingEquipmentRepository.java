package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentRepository extends CrudRepository<CountingEquipmentEntity, Long> {

    Optional<CountingEquipmentEntity> findByCode(String code);

    @Query(value = "SELECT DISTINCT ON (ce.id) " +
            "ce.id AS id, " +
            "ce.code AS code, " +
            "ce.alias AS alias, " +
            "ce.section_id AS section, " +
            "ce.equipment_status AS equipmentStatus, " +
            "ce.p_timer_communication_cycle AS pTimerCommunicationCycle, " +
            "CASE WHEN po.is_completed = false THEN po.code ELSE NULL END AS productionOrderCode, " +
            "eo.id AS outputs " +
            "FROM counting_equipment ce " +
            "LEFT JOIN equipment_output eo ON eo.counting_equipment_id = ce.id " +
            "LEFT JOIN ( " +
            "   SELECT DISTINCT ON (equipment_id) equipment_id, code, is_completed " +
            "   FROM production_order " +
            "   WHERE is_completed = false " +
            "   ORDER BY equipment_id, created_at DESC " +
            ") po ON po.equipment_id = ce.id " +
            "WHERE ce.id = :id " +
            "ORDER BY ce.id, eo.id", nativeQuery = true)
    Optional<CountingEquipmentProjection> findProjectionById(long id);

    @Override
    @EntityGraph(attributePaths = "outputs")
    List<CountingEquipmentEntity> findAll();

    @Query(value = "SELECT DISTINCT ON (ce.id) " +
            "ce.id AS id, " +
            "ce.code AS code, " +
            "ce.alias AS alias, " +
            "ce.section_id AS section, " +
            "ce.equipment_status AS equipmentStatus, " +
            "ce.p_timer_communication_cycle AS pTimerCommunicationCycle, " +
            "CASE WHEN po.is_completed = false THEN po.code ELSE NULL END AS productionOrderCode, " +
            "eo.id AS outputs " +
            "FROM counting_equipment ce " +
            "LEFT JOIN equipment_output eo ON eo.counting_equipment_id = ce.id " +
            "LEFT JOIN ( " +
            "   SELECT DISTINCT ON (equipment_id) equipment_id, code, is_completed " +
            "   FROM production_order " +
            "   WHERE is_completed = false " +
            "   ORDER BY equipment_id, created_at DESC " +
            ") po ON po.equipment_id = ce.id " +
            "ORDER BY ce.id, eo.id", nativeQuery = true)
    List<CountingEquipmentProjection> findAllWithActiveProductionOrderCode();
}

package com.alcegory.mescloud.repository.equipment;

import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentRepository extends CrudRepository<CountingEquipmentEntity, Long> {

    @Query(value = "SELECT SUM(ce.quality_target) / COUNT(ce) FROM counting_equipment ce WHERE ce.section_id = :sectionId",
            nativeQuery = true)
    Double findSumQualityTargetDividedByTotalCount(@Param("sectionId") Long sectionId);

    @Query(value = "SELECT SUM(ce.availability_target) / COUNT(ce) FROM counting_equipment ce WHERE ce.section_id = :sectionId",
            nativeQuery = true)
    Double findSumAvailabilityTargetDividedByTotalCount(@Param("sectionId") Long sectionId);

    @Query(value = "SELECT SUM(ce.performance_target) / COUNT(ce) FROM counting_equipment ce WHERE ce.section_id = :sectionId",
            nativeQuery = true)
    Double findSumPerformanceTargetDividedByTotalCount(@Param("sectionId") Long sectionId);

    @Query(value = "SELECT SUM(ce.overall_equipment_effectiveness_target) / COUNT(ce) FROM counting_equipment ce WHERE ce.section_id = :sectionId",
            nativeQuery = true)
    Double findSumOverallEquipmentEffectivenessTargetDividedByTotalCount(@Param("sectionId") Long sectionId);

    @Query(value = "SELECT SUM(ce.theoretical_production) / COUNT(ce) FROM counting_equipment ce WHERE ce.section_id = :sectionId",
            nativeQuery = true)
    Double findSumTheoreticalProductionDividedByTotalCount(@Param("sectionId") Long sectionId);


    Optional<CountingEquipmentEntity> findByCode(String code);

    @Modifying
    @Query(value = "UPDATE counting_equipment SET operation_status = :status WHERE code = :code", nativeQuery = true)
    int updateOperationStatusByCode(@Param("code") String code, @Param("status") String status);

    @Query("SELECT ce FROM counting_equipment ce LEFT JOIN FETCH ce.equipmentStatusRecords esr " +
            "WHERE ce.code = :code " +
            "AND (esr IS NULL OR esr.id = (SELECT MAX(esr2.id) FROM ce.equipmentStatusRecords esr2))")
    Optional<CountingEquipmentEntity> findByCodeWithLastStatusRecord(@Param("code") String code);

    @Query("SELECT ce FROM counting_equipment ce " +
            "LEFT JOIN FETCH ce.productionOrders po " +
            "WHERE ce.id = :id AND (po IS NULL OR po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment = ce)) " +
            "ORDER BY ce.id")
    Optional<CountingEquipmentEntity> findByIdWithLastProductionOrder(@Param("id") Long id);

    @Query("SELECT ce FROM counting_equipment ce " +
            "LEFT JOIN FETCH ce.productionOrders po " +
            "WHERE (po IS NULL OR po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment = ce)) " +
            "AND ce.section.id = :sectionId " +
            "ORDER BY ce.id")
    List<CountingEquipmentEntity> findAllWithLastProductionOrder(@Param("sectionId") long sectionId);

    @Query("SELECT ce.id FROM counting_equipment ce")
    List<Long> findAllIds();

    @Query("SELECT ce.id FROM counting_equipment ce WHERE ce.alias = :alias")
    Long findIdByAlias(String alias);

    @Query("SELECT ce FROM counting_equipment ce LEFT JOIN FETCH ce.template WHERE ce.id = :id")
    Optional<CountingEquipmentEntity> findEquipmentWithTemplateById(@Param("id") Long id);
}

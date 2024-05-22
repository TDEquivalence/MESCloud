package com.alcegory.mescloud.repository.equipment;

import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountingEquipmentRepository extends CrudRepository<CountingEquipmentEntity, Long> {

    @Query(value = "SELECT AVG(ce.quality_target) / COUNT(ce) FROM counting_equipment ce", nativeQuery = true)
    Double findAverageQualityTargetDividedByTotalCount();

    @Query(value = "SELECT AVG(ce.availability_target) / COUNT(ce) FROM counting_equipment ce", nativeQuery = true)
    Double findAverageAvailabilityTargetDividedByTotalCount();

    @Query(value = "SELECT AVG(ce.performance_target) / COUNT(ce) FROM counting_equipment ce", nativeQuery = true)
    Double findAveragePerformanceTargetDividedByTotalCount();

    @Query(value = "SELECT AVG(ce.overall_equipment_effectiveness_target) / COUNT(ce) FROM counting_equipment ce", nativeQuery = true)
    Double findAverageOverallEquipmentEffectivenessTargetDividedByTotalCount();

    @Query(value = "SELECT AVG(ce.theoretical_production) FROM counting_equipment ce", nativeQuery = true)
    Double findAverageTheoreticalProduction();

    Optional<CountingEquipmentEntity> findByCode(String code);

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
            "WHERE po IS NULL OR po.id = (SELECT MAX(p.id) FROM production_order p WHERE p.equipment = ce)" +
            "ORDER BY ce.id")
    List<CountingEquipmentEntity> findAllWithLastProductionOrder();

    @Query("SELECT ce.id FROM counting_equipment ce")
    List<Long> findAllIds();

    @Query("SELECT ce.id FROM counting_equipment ce WHERE ce.alias = :alias")
    Long findIdByAlias(String alias);

    @Query("SELECT ce FROM counting_equipment ce LEFT JOIN FETCH ce.template WHERE ce.id = :id")
    Optional<CountingEquipmentEntity> findEquipmentWithTemplateById(@Param("id") Long id);
}

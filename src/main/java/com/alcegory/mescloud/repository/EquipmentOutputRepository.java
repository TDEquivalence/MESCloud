package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EquipmentOutputRepository extends JpaRepository<EquipmentOutputEntity, Long> {

    Optional<EquipmentOutputEntity> findByCode(String equipmentOutputCode);

    @Query(value = "SELECT eo.id FROM equipment_output eo WHERE eo.counting_equipment_id = :countingEquipmentId", nativeQuery = true)
    List<Long> findEquipmentOutputIdsByCountingEquipmentId(@Param("countingEquipmentId") Long countingEquipmentId);

    @Query(value = "SELECT eo.id FROM equipment_output eo WHERE eo.counting_equipment_id = :countingEquipmentId ORDER BY eo.id ASC LIMIT 1", nativeQuery = true)
    Long findFirstEquipmentOutputIdByCountingEquipmentId(@Param("countingEquipmentId") Long countingEquipmentId);
}

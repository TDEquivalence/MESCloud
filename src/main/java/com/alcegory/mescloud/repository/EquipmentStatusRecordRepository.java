package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.EquipmentStatusRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EquipmentStatusRecordRepository extends JpaRepository<EquipmentStatusRecordEntity, Long> {

    @Query(value =
            "SELECT * FROM equipment_status_record " +
                    "WHERE counting_equipment_id = :equipmentId " +
                    "AND (" +
                    "    (registered_at >= :startDate AND registered_at <= :endDate) " +
                    "    OR registered_at = (" +
                    "        SELECT MAX(registered_at) " +
                    "        FROM equipment_status_record " +
                    "        WHERE counting_equipment_id = :equipmentId " +
                    "        AND registered_at < :startDate" +
                    "    )" +
                    ") " +
                    "ORDER BY registered_at",
            nativeQuery = true)
    List<EquipmentStatusRecordEntity> findRecordsForPeriodAndLastBefore(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);

    @Query("SELECT e FROM EquipmentStatusRecordEntity e " +
            "WHERE e.countingEquipment.id = :equipmentId " +
            "AND e.equipmentStatus = 1 " +
            "ORDER BY e.registeredAt DESC")
    EquipmentStatusRecordEntity findLastEquipmentStatusWithStatusOne(@Param("equipmentId") Long equipmentId);
}

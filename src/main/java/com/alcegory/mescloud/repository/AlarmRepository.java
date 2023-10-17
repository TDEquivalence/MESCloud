package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.AlarmCounts;
import com.alcegory.mescloud.model.entity.AlarmEntity;
import com.alcegory.mescloud.model.filter.AlarmFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    List<AlarmEntity> findAllByFilter(AlarmFilter filter);

    AlarmCounts getAlarmCounts(AlarmFilter filter);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM alarm a " +
            "WHERE a.alarmConfiguration.id = :alarmConfigurationId " +
            "AND a.equipment.id = :equipmentId " +
            "AND a.status = 'ACTIVE'")
    boolean isAlreadyReported(@Param("alarmConfigurationId") long alarmConfigurationId,
                              @Param("equipmentId") long equipmentId);
}

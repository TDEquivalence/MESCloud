package com.alcegory.mescloud.repository.alarm;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.model.dto.AlarmCountsDto;
import com.alcegory.mescloud.model.entity.AlarmEntity;
import com.alcegory.mescloud.model.entity.AlarmSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    List<AlarmSummaryEntity> findByFilter(Filter filter);

    List<AlarmEntity> findByEquipmentIdAndStatus(Long equipmentId, AlarmStatus status);

    AlarmCountsDto getAlarmCounts(Filter filter);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM alarm a " +
            "WHERE a.alarmConfiguration.id = :alarmConfigurationId " +
            "AND a.equipment.id = :equipmentId " +
            "AND a.status = 'ACTIVE'")
    boolean isAlreadyReported(@Param("alarmConfigurationId") long alarmConfigurationId,
                              @Param("equipmentId") long equipmentId);
}

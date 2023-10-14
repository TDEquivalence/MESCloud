package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.AlarmRecordCounts;
import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRecordRepository extends JpaRepository<AlarmRecordEntity, Long> {

    List<AlarmRecordEntity> findAllByFilter(AlarmRecordFilter filter);

    AlarmRecordCounts getAlarmRecordCounts(AlarmRecordFilter filter);
}

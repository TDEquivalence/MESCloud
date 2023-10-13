package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRecordRepository extends JpaRepository<AlarmRecordEntity, Long> {
}

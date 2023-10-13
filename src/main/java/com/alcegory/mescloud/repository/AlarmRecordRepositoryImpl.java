package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import org.springframework.stereotype.Repository;

@Repository
public class AlarmRecordRepositoryImpl extends AbstractFilterRepository<AlarmRecordFilter.Property, AlarmRecordEntity> {
}

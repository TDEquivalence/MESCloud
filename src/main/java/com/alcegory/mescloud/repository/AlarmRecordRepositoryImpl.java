package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlarmRecordRepositoryImpl extends AbstractFilterRepository<AlarmRecordFilter.Property, AlarmRecordEntity> {

    public List<AlarmRecordEntity> findAllByFilter(AlarmRecordFilter filter) {
        return super.findAllWithFilter(filter, AlarmRecordEntity.class);
    }
}

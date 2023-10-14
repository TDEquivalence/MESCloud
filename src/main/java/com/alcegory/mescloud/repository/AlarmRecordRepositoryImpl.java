package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.model.entity.AlarmRecordCounts;
import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlarmRecordRepositoryImpl extends AbstractFilterRepository<AlarmRecordFilter.Property, AlarmRecordEntity> {

    public List<AlarmRecordEntity> findAllByFilter(AlarmRecordFilter filter) {
        return super.findAllWithFilter(filter, AlarmRecordEntity.class);
    }

    public AlarmRecordCounts getAlarmRecordCounts(AlarmRecordFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AlarmRecordCounts> query = cb.createQuery(AlarmRecordCounts.class);
        Root<AlarmRecordEntity> root = query.from(AlarmRecordEntity.class);

        query.select(cb.construct(AlarmRecordCounts.class,
                cb.count(root),
                cb.sum(cb.<Long>selectCase()
                        .when(cb.equal(root.get("status"), AlarmStatus.ACTIVE), 1L)
                        .otherwise(0L)
                )
        ));

        return entityManager.createQuery(query).getSingleResult();
    }
}

package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.model.entity.AlarmCounts;
import com.alcegory.mescloud.model.entity.AlarmEntity;
import com.alcegory.mescloud.model.filter.AlarmFilter;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AlarmRepositoryImpl extends AbstractFilterRepository<AlarmFilter.Property, AlarmEntity> {

    public List<AlarmEntity> findAllByFilter(AlarmFilter filter) {
        return super.findAllWithFilter(filter, AlarmEntity.class);
    }

    public AlarmCounts getAlarmCounts(AlarmFilter filter) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AlarmCounts> query = criteriaBuilder.createQuery(AlarmCounts.class);
        Root<AlarmEntity> root = query.from(AlarmEntity.class);

        Expression<Long> totalAlarmsExpression = criteriaBuilder.coalesce(criteriaBuilder.count(root), 0L);
        Expression<Long> totalActiveAlarmsExpression = criteriaBuilder.coalesce(
                criteriaBuilder.sum(criteriaBuilder.<Long>selectCase()
                        .when(criteriaBuilder.equal(root.get("status"), AlarmStatus.ACTIVE), 1L)
                        .otherwise(0L)
                ),
                0L
        );

        List<Predicate> predicates = new ArrayList<>();
        super.addPredicates(filter, predicates, criteriaBuilder, root);

        query.select(criteriaBuilder.construct(AlarmCounts.class, totalAlarmsExpression, totalActiveAlarmsExpression))
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }
}

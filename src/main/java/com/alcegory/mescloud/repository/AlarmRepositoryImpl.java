package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.model.dto.AlarmCountsDto;
import com.alcegory.mescloud.model.entity.AlarmEntity;
import com.alcegory.mescloud.model.entity.AlarmSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AlarmRepositoryImpl extends AbstractFilterRepository<Filter.Property, AlarmEntity> {

    private static final String ID_PROP = "id";
    private static final String CREATED_AT_PROP = "createdAt";
    private static final String STATUS_PROP = "status";

    public List<AlarmSummaryEntity> findByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AlarmSummaryEntity> query = criteriaBuilder.createQuery(AlarmSummaryEntity.class);
        Root<AlarmSummaryEntity> root = query.from(AlarmSummaryEntity.class);

        List<Predicate> predicates = buildPredicates(criteriaBuilder, root, filter);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query)
                .getResultList();
    }

    public AlarmCountsDto getAlarmCounts(Filter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AlarmCountsDto> query = criteriaBuilder.createQuery(AlarmCountsDto.class);
        Root<AlarmEntity> root = query.from(AlarmEntity.class);

        Expression<Long> totalAlarmsExpression = getTotalAlarmsExpression(criteriaBuilder, root);
        Expression<Long> totalActiveAlarmsExpression = getTotalActiveAlarmsExpression(criteriaBuilder, root);

        List<Predicate> predicates = buildPredicates(criteriaBuilder, root, filter);

        query.select(criteriaBuilder.construct(AlarmCountsDto.class, totalAlarmsExpression, totalActiveAlarmsExpression))
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }

    private Expression<Long> getTotalAlarmsExpression(CriteriaBuilder criteriaBuilder, Root<AlarmEntity> root) {
        return criteriaBuilder.coalesce(criteriaBuilder.count(root), 0L);
    }

    private Expression<Long> getTotalActiveAlarmsExpression(CriteriaBuilder criteriaBuilder, Root<AlarmEntity> root) {
        return criteriaBuilder.coalesce(
                criteriaBuilder.sum(criteriaBuilder.<Long>selectCase()
                        .when(criteriaBuilder.equal(root.get(STATUS_PROP), AlarmStatus.ACTIVE), 1L)
                        .otherwise(0L)
                ),
                0L
        );
    }

    private List<Predicate> buildPredicates(CriteriaBuilder criteriaBuilder, Root<?> root, Filter filter) {
        List<Predicate> predicates = new ArrayList<>();
        Timestamp startDate = filter.getSearch().getTimestampValue(Filter.Property.START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(Filter.Property.END_DATE);

        predicates.add(criteriaBuilder.between(root.get(CREATED_AT_PROP), startDate, endDate));

        String statusString = filter.getSearch().getValue(Filter.Property.STATUS);
        if (statusString != null && !statusString.isEmpty()) {
            try {
                AlarmStatus statusEnum = AlarmStatus.valueOf(statusString.toUpperCase());
                predicates.add(criteriaBuilder.equal(root.get(STATUS_PROP), statusEnum));
            } catch (IllegalArgumentException e) {
                log.error("Invalid status string: {}", statusString);
            }
        }

        return predicates;
    }
}

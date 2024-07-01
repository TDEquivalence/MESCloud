package com.alcegory.mescloud.repository.record;

import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.equipment.EquipmentOutputEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordDailySummaryEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordDetailedSummaryEntity;
import com.alcegory.mescloud.model.entity.records.CounterRecordEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.repository.AbstractFilterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.alcegory.mescloud.model.filter.Filter.Property.*;

@Repository
@Log
public class CounterRecordRepositoryImpl extends AbstractFilterRepository<Filter.Property, CounterRecordEntity> {

    private static final String ID_PROP = "id";
    private static final String SECTION_ID_PROP = "sectionId";
    private static final String SECTION = "section";
    private static final String EQUIPMENT_OUTPUT_PROP = "equipmentOutput";
    private static final String EQUIPMENT_ID_PROP = "equipmentId";
    private static final String PRODUCTION_ORDER_PROP = "productionOrder";
    private static final String PRODUCTION_ORDER_CODE_PROP = "productionOrderCode";
    private static final String COUNTING_EQUIPMENT_PROP = "countingEquipment";
    private static final String COUNTING_EQUIPMENT_ALIAS_PROP = "alias";
    private static final String REGISTERED_AT_PROP = "registeredAt";
    private static final String IS_VALID_FOR_PRODUCTION_PROP = "isValidForProduction";
    private static final String EQUIPMENT_ALIAS_PROP = "equipmentAlias";
    private static final String START_DATE_PROP = "startDate";
    private static final String END_DATE_PROP = "endDate";

    protected CounterRecordRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public List<CounterRecordDailySummaryEntity> findLastPerProductionOrderAndEquipmentOutputPerDay(long sectionId, FilterDto filter) {
        Timestamp startDateFilter = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDateFilter = filter.getSearch().getTimestampValue(END_DATE);
        String equipmentAlias = filter.getSearch().getValue(EQUIPMENT_ALIAS);
        String productionOrderCode = filter.getSearch().getValue(PRODUCTION_ORDER_CODE);

        String queryString = "SELECT * FROM counter_record_daily_summary WHERE section_id = :sectionId AND registered_at " +
                "BETWEEN :startDate AND :endDate";

        if (equipmentAlias != null && !equipmentAlias.isEmpty()) {
            queryString += " AND equipment_alias = :equipmentAlias";
        }

        if (productionOrderCode != null && !productionOrderCode.isEmpty()) {
            queryString += " AND production_order_code = :productionOrderCode";
        }

        Query query = entityManager.createNativeQuery(queryString, CounterRecordDailySummaryEntity.class);
        query.setParameter(SECTION_ID_PROP, sectionId);
        query.setParameter(START_DATE_PROP, startDateFilter);
        query.setParameter(END_DATE_PROP, endDateFilter);

        if (equipmentAlias != null && !equipmentAlias.isEmpty()) {
            query.setParameter(EQUIPMENT_ALIAS_PROP, equipmentAlias);
        }

        if (productionOrderCode != null && !productionOrderCode.isEmpty()) {
            query.setParameter(PRODUCTION_ORDER_CODE_PROP, productionOrderCode);
        }

        return query.getResultList();
    }

    public List<CounterRecordDetailedSummaryEntity> getFilteredAndPaginated(long sectionId, Filter filterDto) {
        Timestamp startDateFilter = filterDto.getSearch().getTimestampValue(START_DATE);
        Timestamp endDateFilter = filterDto.getSearch().getTimestampValue(END_DATE);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordDetailedSummaryEntity> query = cb.createQuery(CounterRecordDetailedSummaryEntity.class);
        Root<CounterRecordDetailedSummaryEntity> root = query.from(CounterRecordDetailedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(SECTION_ID_PROP), sectionId));

        if (startDateFilter != null && endDateFilter != null) {
            predicates.add(cb.between(root.get(REGISTERED_AT_PROP), startDateFilter, endDateFilter));
        } else if (startDateFilter != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(REGISTERED_AT_PROP), startDateFilter));
        } else if (endDateFilter != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(REGISTERED_AT_PROP), endDateFilter));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake())
                .getResultList();
    }

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(long sectionId, Filter filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordConclusionEntity> query = criteriaBuilder.createQuery(CounterRecordConclusionEntity.class);
        Root<CounterRecordConclusionEntity> root = query.from(CounterRecordConclusionEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get(SECTION_ID_PROP), sectionId));
        addPredicates(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        addSortOrders(filterDto, orders, criteriaBuilder, root);
        Order newestOrder = criteriaBuilder.desc(root.get(ID_PROP));
        orders.add(newestOrder);

        root.fetch(EQUIPMENT_OUTPUT_PROP, JoinType.LEFT);
        root.fetch(PRODUCTION_ORDER_PROP, JoinType.LEFT);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake())
                .getResultList();
    }

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(long sectionId, FilterDto filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordConclusionEntity> query = criteriaBuilder.createQuery(CounterRecordConclusionEntity.class);
        Root<CounterRecordConclusionEntity> root = query.from(CounterRecordConclusionEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get(SECTION_ID_PROP), sectionId));
        addPredicates(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        Order newestOrder = criteriaBuilder.desc(root.get(ID_PROP));
        orders.add(newestOrder);

        root.fetch(EQUIPMENT_OUTPUT_PROP, JoinType.LEFT);
        root.fetch(PRODUCTION_ORDER_PROP, JoinType.LEFT);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query)
                .getResultList();
    }

    @Override
    protected <T> void populatePathByJointProperty() {

        pathByJointProperty.put(Filter.Property.EQUIPMENT_ALIAS.getEntityProperty(),
                r -> {
                    Join<T, EquipmentOutputEntity> equipmentOutputJoin = r.join(EQUIPMENT_OUTPUT_PROP);
                    Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                            equipmentOutputJoin.join(COUNTING_EQUIPMENT_PROP);
                    return countingEquipmentJoin.get(COUNTING_EQUIPMENT_ALIAS_PROP);
                });

        pathByJointProperty.put(PRODUCTION_ORDER_CODE.getEntityProperty(),
                r -> {
                    Join<T, ProductionOrderEntity> productionOrderJoin = r.join(PRODUCTION_ORDER_PROP);
                    return productionOrderJoin.get(PRODUCTION_ORDER_CODE_PROP);
                });
    }

    //KPI QUALITY WITH COUNTER_RECORD_SUMMARY_VIEW//
    public Integer sumIncrementDay(Long sectionId, Long equipmentId, FilterDto filter, boolean filterByValidProduction) {
        Timestamp startDateFilter = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDateFilter = filter.getSearch().getTimestampValue(END_DATE);
        String productionOrderCode = filter.getSearch().getValue(PRODUCTION_ORDER_CODE);
        String equipmentAlias = filter.getSearch().getValue(EQUIPMENT_ALIAS);

        StringBuilder nativeQuery = new StringBuilder("SELECT SUM(increment_day) FROM counter_record_daily_summary c WHERE ");

        List<String> conditions = new ArrayList<>();

        if (filterByValidProduction) {
            conditions.add("c.is_valid_for_production = true");
        }

        if (equipmentId != null) {
            conditions.add("c.equipment_id = :equipmentId");
        }

        if (productionOrderCode != null) {
            conditions.add("c.production_order_code = :productionOrderCode");
        }

        if (equipmentAlias != null) {
            conditions.add("c.equipment_alias = :equipmentAlias");
        }

        if (sectionId != null) {
            conditions.add("c.section_id = :sectionId");
        }

        conditions.add("c.registered_at BETWEEN :startDate AND :endDate");

        nativeQuery.append(String.join(" AND ", conditions));

        Query query = entityManager.createNativeQuery(nativeQuery.toString())
                .setParameter(START_DATE_PROP, startDateFilter)
                .setParameter(END_DATE_PROP, endDateFilter);

        if (equipmentId != null) {
            query.setParameter(EQUIPMENT_ID_PROP, equipmentId);
        }

        if (productionOrderCode != null) {
            query.setParameter(PRODUCTION_ORDER_CODE_PROP, productionOrderCode);
        }

        if (equipmentAlias != null) {
            query.setParameter(EQUIPMENT_ALIAS_PROP, equipmentAlias);
        }

        if (sectionId != null) {
            query.setParameter(SECTION_ID_PROP, sectionId);
        }

        Object result = query.getSingleResult();
        return result != null ? ((Number) result).intValue() : 0;
    }

    //KPI AVAILABILITY WITH COUNTER_RECORD_SUMMARY_VIEW//
    public List<CounterRecordDailySummaryEntity> findBySectionAndEquipmentAndPeriod(Long sectionId, Long equipmentId, FilterDto filter) {
        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);
        String productionOrderCode = filter.getSearch().getValue(PRODUCTION_ORDER_CODE);
        String equipmentAlias = filter.getSearch().getValue(EQUIPMENT_ALIAS);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordDailySummaryEntity> criteriaQuery = criteriaBuilder.createQuery(CounterRecordDailySummaryEntity.class);
        Root<CounterRecordDailySummaryEntity> root = criteriaQuery.from(CounterRecordDailySummaryEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (sectionId != null) {
            predicates.add(criteriaBuilder.equal(root.get(SECTION_ID_PROP), sectionId));
        }

        if (equipmentId != null) {
            predicates.add(criteriaBuilder.equal(root.get(EQUIPMENT_ID_PROP), equipmentId));
        }
        if (productionOrderCode != null) {
            predicates.add(criteriaBuilder.equal(root.get(PRODUCTION_ORDER_CODE_PROP), productionOrderCode));
        }
        if (equipmentAlias != null) {
            predicates.add(criteriaBuilder.equal(root.get(EQUIPMENT_ALIAS_PROP), equipmentAlias));
        }
        predicates.add(criteriaBuilder.between(root.get(REGISTERED_AT_PROP), startDate, endDate));
        predicates.add(criteriaBuilder.isTrue(root.get(IS_VALID_FOR_PRODUCTION_PROP)));

        criteriaQuery.select(root)
                .distinct(true)
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
package com.alcegory.mescloud.repository.record;

import com.alcegory.mescloud.model.dto.FilterDto;
import com.alcegory.mescloud.model.entity.*;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.AbstractFilterRepository;
import com.alcegory.mescloud.utility.DateUtil;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

import static com.alcegory.mescloud.model.filter.Filter.Property.*;

@Repository
@Log
public class CounterRecordRepositoryImpl extends AbstractFilterRepository<Filter.Property, CounterRecordEntity> {

    private static final String ID_PROP = "id";
    private static final String EQUIPMENT_OUTPUT_PROP = "equipmentOutput";
    private static final String EQUIPMENT_OUTPUT_ALIAS_PROP = "equipmentOutputAlias";
    private static final String PRODUCTION_ORDER_PROP = "productionOrder";
    private static final String COUNTING_EQUIPMENT_PROP = "countingEquipment";
    private static final String PRODUCTION_ORDER_CODE_PROP = "code";
    private static final String COUNTING_EQUIPMENT_ALIAS_PROP = "alias";
    private static final String REGISTERED_AT_PROP = "registeredAt";
    private static final String IS_VALID_FOR_PRODUCTION_PROP = "isValidForProduction";
    private static final String INCREMENT_PROP = "increment";


    public List<CounterRecordSummaryEntity> findLastPerProductionOrderAndEquipmentOutputPerDay(FilterDto filter) {
        String startDateStr = filter.getSearch().getValue(START_DATE);
        Date startDate = Date.from(DateUtil.convertToInstant(startDateStr));
        String endDateStr = filter.getSearch().getValue(END_DATE);
        Date endDate = Date.from(DateUtil.convertToInstant(endDateStr));

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordSummaryEntity> criteriaQuery = criteriaBuilder.createQuery(CounterRecordSummaryEntity.class);
        Root<CounterRecordSummaryEntity> root = criteriaQuery.from(CounterRecordSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        Predicate dateRangePredicate = criteriaBuilder.between(root.get(REGISTERED_AT_PROP), startDate, endDate);
        predicates.add(dateRangePredicate);

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<CounterRecordEntity> getFilteredAndPaginated(Filter filterDto) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> query = cb.createQuery(CounterRecordEntity.class);
        Root<CounterRecordEntity> root = query.from(CounterRecordEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filterDto, predicates, cb, root);

        List<Order> orders = new ArrayList<>();
        addSortOrders(filterDto, orders, cb, root);
        Order newestOrder = cb.desc(root.get(ID_PROP));
        orders.add(newestOrder);

        EntityGraph<CounterRecordEntity> entityGraph = entityManager.createEntityGraph(CounterRecordEntity.class);
        entityGraph.addSubgraph(PRODUCTION_ORDER_PROP);
        entityGraph.addSubgraph(EQUIPMENT_OUTPUT_PROP).addSubgraph(COUNTING_EQUIPMENT_PROP);

        query.select(root)
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query)
                .setHint(JAKARTA_FETCHGRAPH, entityGraph)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake())
                .getResultList();
    }


    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(Filter filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordConclusionEntity> query = criteriaBuilder.createQuery(CounterRecordConclusionEntity.class);
        Root<CounterRecordConclusionEntity> root = query.from(CounterRecordConclusionEntity.class);

        List<Predicate> predicates = new ArrayList<>();
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

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(FilterDto filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordConclusionEntity> query = criteriaBuilder.createQuery(CounterRecordConclusionEntity.class);
        Root<CounterRecordConclusionEntity> root = query.from(CounterRecordConclusionEntity.class);

        List<Predicate> predicates = new ArrayList<>();
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

    public Integer sumCounterIncrement(Long countingEquipmentId, FilterDto filter) {

        Timestamp startDateFilter = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDateFilter = filter.getSearch().getTimestampValue(END_DATE);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);

        Root<CounterRecordEntity> crRoot = query.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> eoJoin = crRoot.join(EQUIPMENT_OUTPUT_PROP, JoinType.INNER);
        Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin = eoJoin.join(COUNTING_EQUIPMENT_PROP, JoinType.INNER);
        Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin = crRoot.join(PRODUCTION_ORDER_PROP, JoinType.INNER);

        Expression<Integer> sumIncrement = cb.sum(crRoot.get(INCREMENT_PROP));

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(countingEquipmentJoin.get("id"), countingEquipmentId));
        predicateList.add(cb.greaterThan(crRoot.get(REGISTERED_AT_PROP), startDateFilter));
        predicateList.add(cb.lessThanOrEqualTo(crRoot.get(REGISTERED_AT_PROP), endDateFilter));

        String productionOrderCode = filter.getSearch().getValue(PRODUCTION_ORDER_CODE);
        if (productionOrderCode != null && !productionOrderCode.isEmpty()) {
            predicateList.add(cb.equal(productionOrderJoin.get("code"), productionOrderCode));
        }

        String equipmentOutputAlias = filter.getSearch().getValue(EQUIPMENT_OUTPUT_ALIAS);
        if (equipmentOutputAlias != null && !equipmentOutputAlias.isEmpty()) {
            predicateList.add(cb.equal(crRoot.get(EQUIPMENT_OUTPUT_ALIAS_PROP), equipmentOutputAlias));
        }

        query.select(sumIncrement).where(predicateList.toArray(new Predicate[0]));

        Integer sum = entityManager.createQuery(query).getSingleResult();
        return sum != null ? sum : 0;
    }

    public Integer sumValidCounterIncrement(Long countingEquipmentId, FilterDto filter) {
        Integer resultSumIncrement = sumValidCounterIncrementByPO(countingEquipmentId, filter);

        return Objects.requireNonNullElse(resultSumIncrement, 0);
    }

    public Integer sumValidCounterIncrementByPO(Long countingEquipmentId, FilterDto filter) {

        Timestamp startDateFilter = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDateFilter = filter.getSearch().getTimestampValue(END_DATE);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);

        Root<CounterRecordEntity> crRoot = query.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> eoJoin = crRoot.join(EQUIPMENT_OUTPUT_PROP, JoinType.INNER);
        Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin = eoJoin.join(COUNTING_EQUIPMENT_PROP, JoinType.INNER);
        Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin = crRoot.join(PRODUCTION_ORDER_PROP, JoinType.INNER);

        Expression<Integer> sumIncrementByPO = cb.sum(crRoot.get(INCREMENT_PROP));

        List<Predicate> predicateList = new ArrayList<>();

        Predicate conditions = cb.and(
                cb.isTrue(crRoot.get(IS_VALID_FOR_PRODUCTION_PROP)),
                cb.equal(countingEquipmentJoin.get("id"), countingEquipmentId)
        );

        Predicate startDate = cb.greaterThan(crRoot.get(REGISTERED_AT_PROP), startDateFilter);
        Predicate endDate = cb.lessThanOrEqualTo(crRoot.get(REGISTERED_AT_PROP), endDateFilter);

        String productionOrderCode = filter.getSearch().getValue(PRODUCTION_ORDER_CODE);
        if (productionOrderCode != null && !productionOrderCode.isEmpty()) {
            predicateList.add(cb.equal(productionOrderJoin.get(PRODUCTION_ORDER_CODE_PROP), productionOrderCode));
        }

        String equipmentOutputAlias = filter.getSearch().getValue(EQUIPMENT_OUTPUT_ALIAS);
        if (equipmentOutputAlias != null && !equipmentOutputAlias.isEmpty()) {
            predicateList.add(cb.equal(crRoot.get(EQUIPMENT_OUTPUT_ALIAS_PROP), equipmentOutputAlias));
        }

        predicateList.add(conditions);
        predicateList.add(startDate);
        predicateList.add(endDate);

        query.select(sumIncrementByPO)
                .where(cb.and(predicateList.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }


    public Integer sumValidCounterIncrementForApprovedPO(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter) {
        Map<Long, Integer> incrementWithApprovedPO = sumValidCounterIncrementWithApprovedPO(countingEquipmentId, startDateFilter, endDateFilter);

        return incrementWithApprovedPO.values().stream().mapToInt(Integer::intValue).sum();
    }

    private Map<Long, Integer> sumValidCounterIncrementWithApprovedPO(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<CounterRecordEntity> crRoot = query.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> eoJoin = crRoot.join(EQUIPMENT_OUTPUT_PROP, JoinType.INNER);
        Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin = eoJoin.join(COUNTING_EQUIPMENT_PROP, JoinType.INNER);
        Join<CounterRecordEntity, ProductionOrderEntity> poJoin = crRoot.join(PRODUCTION_ORDER_PROP, JoinType.INNER);

        Expression<Long> productionOrderId = poJoin.get("id");
        Expression<Integer> sumIncrement = cb.sum(crRoot.get(INCREMENT_PROP));

        List<Predicate> predicateList = new ArrayList<>();
        Predicate conditions = cb.and(
                cb.isTrue(crRoot.get(IS_VALID_FOR_PRODUCTION_PROP)),
                cb.equal(countingEquipmentJoin.get("id"), countingEquipmentId),
                cb.isTrue(poJoin.get("isApproved"))
        );

        Predicate startDate = cb.greaterThan(crRoot.get(REGISTERED_AT_PROP), startDateFilter);
        Predicate endDate = cb.lessThanOrEqualTo(crRoot.get(REGISTERED_AT_PROP), endDateFilter);
        predicateList.add(conditions);
        predicateList.add(startDate);
        predicateList.add(endDate);

        query.multiselect(productionOrderId, sumIncrement)
                .where(cb.and(predicateList.toArray(new Predicate[0])))
                .groupBy(productionOrderId);

        List<Tuple> result = entityManager.createQuery(query).getResultList();

        Map<Long, Integer> incrementByPO = new HashMap<>();
        for (Tuple tuple : result) {
            Long productionOrderIdValue = tuple.get(productionOrderId);
            Integer totalIncrement = tuple.get(sumIncrement);
            incrementByPO.put(productionOrderIdValue, totalIncrement);
        }

        return incrementByPO;
    }

}
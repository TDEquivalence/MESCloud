package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilter;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.entity.*;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CounterRecordRepositoryImpl extends AbstractFilterRepository<CounterRecordFilter.Property, CounterRecordEntity> {

    private static final String ID_PROP = "id";
    private static final String EQUIPMENT_OUTPUT_PROP = "equipmentOutput";
    private static final String PRODUCTION_ORDER_PROP = "productionOrder";
    private static final String COUNTING_EQUIPMENT_PROP = "countingEquipment";
    private static final String PRODUCTION_ORDER_CODE_PROP = "code";
    private static final String COUNTING_EQUIPMENT_ALIAS_PROP = "alias";


    public List<CounterRecordEntity> getFilteredAndPaginated(CounterRecordFilter filterDto) {
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

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(CounterRecordFilter filterDto) {

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

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(KpiFilterDto filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordConclusionEntity> query = criteriaBuilder.createQuery(CounterRecordConclusionEntity.class);
        Root<CounterRecordConclusionEntity> root = query.from(CounterRecordConclusionEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        //TODO: Implement sort
        //addSortOrdersConclusion(filterDto, orders, criteriaBuilder, root);
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

        pathByJointProperty.put(CounterRecordFilter.Property.EQUIPMENT_ALIAS.getEntityProperty(),
                r -> {
                    Join<T, EquipmentOutputEntity> equipmentOutputJoin = r.join(EQUIPMENT_OUTPUT_PROP);
                    Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                            equipmentOutputJoin.join(COUNTING_EQUIPMENT_PROP);
                    return countingEquipmentJoin.get(COUNTING_EQUIPMENT_ALIAS_PROP);
                });

        pathByJointProperty.put(CounterRecordFilter.Property.PRODUCTION_ORDER_CODE.getEntityProperty(),
                r -> {
                    Join<T, ProductionOrderEntity> productionOrderJoin = r.join(PRODUCTION_ORDER_PROP);
                    return productionOrderJoin.get(PRODUCTION_ORDER_CODE_PROP);
                });
    }

    public List<CounterRecordEntity> getAllMaxValidCounterRecordByEquipmentId(Long countingEquipmentId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> criteriaQuery = criteriaBuilder.createQuery(CounterRecordEntity.class);
        Root<CounterRecordEntity> counterRecordRoot = criteriaQuery.from(CounterRecordEntity.class);
        criteriaQuery.select(counterRecordRoot);

        Join<CounterRecordEntity, EquipmentOutputEntity> equipmentOutputJoin = counterRecordRoot.join("equipmentOutput");
        Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin = equipmentOutputJoin.join("countingEquipment");

        Predicate conditions = criteriaBuilder.and(
                criteriaBuilder.equal(countingEquipmentJoin.get("id"), countingEquipmentId),
                criteriaBuilder.isTrue(counterRecordRoot.get("isValidForProduction"))
        );

        Subquery<Integer> maxComputedValueSubquery = criteriaQuery.subquery(Integer.class);
        Root<CounterRecordEntity> maxComputedValueSubqueryRoot = maxComputedValueSubquery.from(CounterRecordEntity.class);
        maxComputedValueSubquery.select(criteriaBuilder.max(maxComputedValueSubqueryRoot.get("computedValue")));
        maxComputedValueSubquery.where(
                criteriaBuilder.equal(maxComputedValueSubqueryRoot.get("equipmentOutput"), equipmentOutputJoin),
                criteriaBuilder.equal(maxComputedValueSubqueryRoot.get("productionOrder"), counterRecordRoot.get("productionOrder"))
        );

        Predicate countCondition = criteriaBuilder.greaterThanOrEqualTo(
                maxComputedValueSubquery,
                criteriaBuilder.literal(1) // Compare with the constant value 1
        );

        conditions = criteriaBuilder.and(conditions, countCondition);

        criteriaQuery.where(conditions);

        TypedQuery<CounterRecordEntity> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

}
package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilter;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.entity.*;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Integer calculateIncrement(Long countingEquipmentId) {
        Map<Long, Integer> incrementByPO = calculateIncrementByPO(countingEquipmentId);

        // Calculate the total increment by summing up the values
        int totalIncrement = incrementByPO.values().stream().mapToInt(Integer::intValue).sum();

        return totalIncrement;
    }


    public Map<Long, Integer> calculateIncrementByPO(Long countingEquipmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<CounterRecordEntity> crRoot = query.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> eoJoin = crRoot.join("equipmentOutput", JoinType.INNER);
        Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin = eoJoin.join("countingEquipment", JoinType.INNER);
        Join<CounterRecordEntity, ProductionOrderEntity> poJoin = crRoot.join("productionOrder", JoinType.INNER);

        // Define the selection for SUM(cr.increment) grouped by production order
        Expression<Integer> sumIncrementByPO = cb.sum(crRoot.get("increment"));

        // Define the WHERE clause conditions
        Predicate conditions = cb.and(
                cb.isTrue(eoJoin.get("isValidForProduction")),
                cb.equal(countingEquipmentJoin.get("id"), countingEquipmentId)
        );

        // Specify the grouping by production order
        query.multiselect(poJoin.get("id"), sumIncrementByPO)
                .where(conditions)
                .groupBy(poJoin.get("id"));

        List<Tuple> result = entityManager.createQuery(query).getResultList();

        // Convert the result into a map for easier processing
        Map<Long, Integer> incrementByPO = new HashMap<>();
        for (Tuple tuple : result) {
            Long productionOrderId = tuple.get(poJoin.get("id"));
            Integer totalIncrement = tuple.get(sumIncrementByPO);
            incrementByPO.put(productionOrderId, totalIncrement);
        }

        return incrementByPO;
    }



    public Integer calculateIncrementWithApprovedPO(Long countingEquipmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);

        Root<CounterRecordEntity> crRoot = query.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> eoJoin = crRoot.join("equipmentOutput", JoinType.INNER);
        Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin = eoJoin.join("countingEquipment", JoinType.INNER);
        Join<CounterRecordEntity, ProductionOrderEntity> poJoin = crRoot.join("productionOrder", JoinType.INNER);

        // Define the selection for SUM(cr.increment)
        Expression<Integer> sumIncrement = cb.sum(crRoot.get("increment"));

        // Define the WHERE clause conditions
        Predicate conditions = cb.and(
                cb.isTrue(eoJoin.get("isValidForProduction")),
                cb.equal(countingEquipmentJoin.get("id"), countingEquipmentId),
                cb.isNotNull(poJoin.get("isApproved"))
        );

        // Specify the grouping
        query.select(sumIncrement)
                .where(conditions)
                .groupBy(poJoin.get("id"));

        // Execute the query
        return entityManager.createQuery(query).getSingleResult();
    }
}
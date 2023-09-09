package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordWinnow;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.model.dto.filter.Searchable;
import com.tde.mescloud.model.dto.filter.Sortable;
import com.tde.mescloud.model.entity.*;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class CounterRecordRepositoryImpl {

    private EntityManager entityManager;

    private static final String ID_PROP = "id";
    private static final String REGISTERED_AT_PROP = "registeredAt";
    private static final String COMPUTED_VALUE_PROP = "computedValue";
    private static final String EQUIPMENT_OUTPUT_PROP = "equipmentOutput";
    private static final String PRODUCTION_ORDER_PROP = "productionOrder";
    private static final String COUNTING_EQUIPMENT_PROP = "countingEquipment";
    private static final String PRODUCTION_ORDER_CODE_PROP = "code";
    private static final String COUNTING_EQUIPMENT_ALIAS_PROP = "alias";

    private static final String START_DATE_FILTER_FIELD = "startDate";
    private static final String END_DATE_FILTER_FIELD = "endDate";
    private static final String EQUIPMENT_ALIAS_FILTER_FIELD = "equipmentAlias";
    private static final String PRODUCTION_ORDER_CODE_FILTER_FIELD = "productionOrderCode";

    private static final String JAKARTA_FETCHGRAPH = "jakarta.persistence.fetchgraph";
    private static final String SQL_WILDCARD = "%";


    public List<CounterRecordEntity> getFilteredAndPaginated(CounterRecordWinnow filterDto) {
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

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(CounterRecordWinnow filterDto) {

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

    private <T> void addPredicates(Searchable<CounterRecordWinnow.Property> filter,
                                   List<Predicate> predicates,
                                   CriteriaBuilder criteriaBuilder,
                                   Root<T> counterRecordRoot) {

        for (CounterRecordWinnow.Property counterRecordProperty : filter.getSearch().getKeys()) {
            Predicate predicate;
            switch (counterRecordProperty.getName()) {
                case COMPUTED_VALUE_PROP -> {
                    int computedValue = Integer.parseInt(filter.getSearch().getValue(counterRecordProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(COMPUTED_VALUE_PROP), computedValue);
                }
                case START_DATE_FILTER_FIELD -> {
                    ZonedDateTime dateStart = ZonedDateTime.parse(filter.getSearch().getValue(counterRecordProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateStart);
                }
                case END_DATE_FILTER_FIELD -> {
                    ZonedDateTime dateEnd = ZonedDateTime.parse(filter.getSearch().getValue(counterRecordProperty));
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateEnd);
                }
                default -> {
                    Path<?> path = getPath(counterRecordRoot, counterRecordProperty.getName());
                    String value = filter.getSearch().getValue(counterRecordProperty).toUpperCase();
                    predicate = createLikePredicate(path, value, criteriaBuilder);
                }
            }
            predicates.add(predicate);
        }
    }

    private Predicate createLikePredicate(Path<?> path, String value, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), value.toUpperCase());
    }

    private <T> void addSortOrders(Sortable<CounterRecordWinnow.Property> filter,
                                   List<Order> orders,
                                   CriteriaBuilder criteriaBuilder,
                                   Root<T> counterRecordRoot) {

        for (CounterRecordWinnow.Property counterRecordProperty : filter.getSort().getKeys()) {

            Order order = filter.getSort().isDescendingSort(counterRecordProperty) ?
                    criteriaBuilder.desc(getPath(counterRecordRoot, counterRecordProperty.getName())) :
                    criteriaBuilder.asc(getPath(counterRecordRoot, counterRecordProperty.getName()));
            orders.add(order);
        }
    }

    private <T> Path<?> getPath(Root<T> counterRecordRoot, String property) {
        switch (property) {
            case EQUIPMENT_ALIAS_FILTER_FIELD -> {
                Join<T, EquipmentOutputEntity> equipmentOutputJoin =
                        counterRecordRoot.join(EQUIPMENT_OUTPUT_PROP);
                Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                        equipmentOutputJoin.join(COUNTING_EQUIPMENT_PROP);
                return countingEquipmentJoin.get(COUNTING_EQUIPMENT_ALIAS_PROP);
            }
            case PRODUCTION_ORDER_CODE_FILTER_FIELD -> {
                Join<T, ProductionOrderEntity> productionOrderJoin =
                        counterRecordRoot.join(PRODUCTION_ORDER_PROP);
                return productionOrderJoin.get(PRODUCTION_ORDER_CODE_PROP);
            }
            default -> {
                return counterRecordRoot.get(property);
            }
        }
    }
}
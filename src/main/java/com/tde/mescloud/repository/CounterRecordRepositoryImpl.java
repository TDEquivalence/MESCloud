package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilterDto;
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

    private static final String DATE_START_FILTER_FIELD = "startDate";
    private static final String DATE_END_FILTER_FIELD = "endDate";
    private static final String EQUIPMENT_ALIAS_FILTER_FIELD = "equipmentAlias";
    private static final String PRODUCTION_ORDER_CODE_FILTER_FIELD = "productionOrderCode";

    private static final String JAKARTA_FETCHGRAPH = "jakarta.persistence.fetchgraph";
    private static final String SQL_WILDCARD = "%";


    public List<CounterRecordEntity> getFilteredAndPaginated(CounterRecordFilterDto filterDto) {

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

    public List<CounterRecordConclusionEntity> findLastPerProductionOrder(CounterRecordFilterDto filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordConclusionEntity> query = criteriaBuilder.createQuery(CounterRecordConclusionEntity.class);
        Root<CounterRecordConclusionEntity> root = query.from(CounterRecordConclusionEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        addPredicatesConclusion(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        addSortOrdersConclusion(filterDto, orders, criteriaBuilder, root);
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

    private void addPredicates(CounterRecordFilterDto filter,
                               List<Predicate> predicates,
                               CriteriaBuilder criteriaBuilder,
                               Root<CounterRecordEntity> counterRecordRoot) {

        for (CounterRecordFilterDto.CounterRecordProperty counterRecordProperty : filter.getSearchKeys()) {
            Predicate predicate;
            switch (counterRecordProperty.getPropertyName()) {
                case COMPUTED_VALUE_PROP -> {
                    int computedValue = Integer.parseInt(filter.getSearchValue(counterRecordProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(COMPUTED_VALUE_PROP), computedValue);
                }
                case DATE_START_FILTER_FIELD -> {
                    ZonedDateTime dateStart = ZonedDateTime.parse(filter.getSearchValue(counterRecordProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateStart);
                }
                case DATE_END_FILTER_FIELD -> {
                    ZonedDateTime dateEnd = ZonedDateTime.parse(filter.getSearchValue(counterRecordProperty));
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateEnd);
                }
                default -> {
                    Path<?> path = getPath(counterRecordRoot, counterRecordProperty.getPropertyName());
                    String value = filter.getSearchValue(counterRecordProperty).toUpperCase();
                    predicate = createLikePredicate(path, value, criteriaBuilder);
                }
            }
            predicates.add(predicate);
        }
    }

    private void addPredicatesConclusion(CounterRecordFilterDto filter,
                                         List<Predicate> predicates,
                                         CriteriaBuilder criteriaBuilder,
                                         Root<CounterRecordConclusionEntity> counterRecordRoot) {

        for (CounterRecordFilterDto.CounterRecordProperty counterRecordProperty : filter.getSearchKeys()) {
            Predicate predicate;
            switch (counterRecordProperty.getPropertyName()) {
                case COMPUTED_VALUE_PROP -> {
                    int computedValue = Integer.parseInt(filter.getSearchValue(counterRecordProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(COMPUTED_VALUE_PROP), computedValue);
                }
                case DATE_START_FILTER_FIELD -> {
                    ZonedDateTime dateStart = ZonedDateTime.parse(filter.getSearchValue(counterRecordProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateStart);
                }
                case DATE_END_FILTER_FIELD -> {
                    ZonedDateTime dateEnd = ZonedDateTime.parse(filter.getSearchValue(counterRecordProperty));
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateEnd);
                }
                default -> {
                    Path<?> path = getPathConclusion(counterRecordRoot, counterRecordProperty.getPropertyName());
                    String value = filter.getSearchValue(counterRecordProperty).toUpperCase();
                    predicate = createLikePredicate(path, value, criteriaBuilder);
                }
            }
            predicates.add(predicate);
        }
    }

    private Predicate createLikePredicate(Path<?> path, String value, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), value.toUpperCase());
    }

    private void addSortOrders(CounterRecordFilterDto filter,
                               List<Order> orders,
                               CriteriaBuilder criteriaBuilder,
                               Root<CounterRecordEntity> counterRecordRoot) {

        for (CounterRecordFilterDto.CounterRecordProperty counterRecordProperty : filter.getSortKeys()) {

            Order order = filter.isDescendingSort(counterRecordProperty) ?
                    criteriaBuilder.desc(getPath(counterRecordRoot, counterRecordProperty.getPropertyName())) :
                    criteriaBuilder.asc(getPath(counterRecordRoot, counterRecordProperty.getPropertyName()));
            orders.add(order);
        }
    }

    private void addSortOrdersConclusion(CounterRecordFilterDto filter,
                                         List<Order> orders,
                                         CriteriaBuilder criteriaBuilder,
                                         Root<CounterRecordConclusionEntity> counterRecordRoot) {

        for (CounterRecordFilterDto.CounterRecordProperty counterRecordProperty : filter.getSortKeys()) {

            Order order = filter.isDescendingSort(counterRecordProperty) ?
                    criteriaBuilder.desc(getPathConclusion(counterRecordRoot, counterRecordProperty.getPropertyName())) :
                    criteriaBuilder.asc(getPathConclusion(counterRecordRoot, counterRecordProperty.getPropertyName()));
            orders.add(order);
        }
    }

    private Path<?> getPath(Root<CounterRecordEntity> counterRecordRoot, String property) {
        switch (property) {
            case EQUIPMENT_ALIAS_FILTER_FIELD -> {
                Join<CounterRecordEntity, EquipmentOutputEntity> equipmentOutputJoin =
                        counterRecordRoot.join(EQUIPMENT_OUTPUT_PROP);
                Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                        equipmentOutputJoin.join(COUNTING_EQUIPMENT_PROP);
                return countingEquipmentJoin.get(COUNTING_EQUIPMENT_ALIAS_PROP);
            }
            case PRODUCTION_ORDER_CODE_FILTER_FIELD -> {
                Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin =
                        counterRecordRoot.join(PRODUCTION_ORDER_PROP);
                return productionOrderJoin.get(PRODUCTION_ORDER_CODE_PROP);
            }
            default -> {
                return counterRecordRoot.get(property);
            }
        }
    }

    private Path<?> getPathConclusion(Root<CounterRecordConclusionEntity> counterRecordRoot, String property) {
        switch (property) {
            case EQUIPMENT_ALIAS_FILTER_FIELD -> {
                Join<CounterRecordConclusionEntity, EquipmentOutputEntity> equipmentOutputJoin =
                        counterRecordRoot.join(EQUIPMENT_OUTPUT_PROP);
                Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                        equipmentOutputJoin.join(COUNTING_EQUIPMENT_PROP);
                return countingEquipmentJoin.get(COUNTING_EQUIPMENT_ALIAS_PROP);
            }
            case PRODUCTION_ORDER_CODE_FILTER_FIELD -> {
                Join<CounterRecordConclusionEntity, ProductionOrderEntity> productionOrderJoin =
                        counterRecordRoot.join(PRODUCTION_ORDER_PROP);
                return productionOrderJoin.get(PRODUCTION_ORDER_CODE_PROP);
            }
            default -> {
                return counterRecordRoot.get(property);
            }
        }
    }
}
package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.CounterRecordSearchDto;
import com.tde.mescloud.model.dto.CounterRecordSortDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
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

    private static final String DATE_END_FILTER_FIELD = "dateEnd";
    private static final String DATE_START_FILTER_FIELD = "dateStart";
    private static final String EQUIPMENT_ALIAS_FILTER_FIELD = "equipmentAlias";
    private static final String PRODUCTION_ORDER_CODE_FILTER_FIELD = "productionOrderCode";

    private static final String SQL_WILDCARD = "%";

    public List<CounterRecordEntity> findByCriteria(CounterRecordFilterDto filterDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> criteriaQuery = criteriaBuilder.createQuery(CounterRecordEntity.class);
        Root<CounterRecordEntity> root = criteriaQuery.from(CounterRecordEntity.class);

        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<CounterRecordEntity> subqueryRoot = subquery.from(CounterRecordEntity.class);
        subquery.select(criteriaBuilder.count(subqueryRoot.get(ID_PROP)));
        subquery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(EQUIPMENT_OUTPUT_PROP), subqueryRoot.get(EQUIPMENT_OUTPUT_PROP)),
                        criteriaBuilder.greaterThan(subqueryRoot.get(REGISTERED_AT_PROP), root.get(REGISTERED_AT_PROP))
                )
        );

        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        addSortOrders(filterDto.getSort(), orders, criteriaBuilder, root);
        orders.add(criteriaBuilder.asc(subquery));
        orders.add(criteriaBuilder.asc(root.get(EQUIPMENT_OUTPUT_PROP).get(ID_PROP)));

        criteriaQuery.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake() + 1)
                .getResultList();
    }


    public List<CounterRecordEntity> findLastPerProductionOrder(CounterRecordFilterDto filterDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> criteriaQuery = criteriaBuilder.createQuery(CounterRecordEntity.class);

        Root<CounterRecordEntity> root = criteriaQuery.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> equipmentOutputJoin = root.join(EQUIPMENT_OUTPUT_PROP);
        Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin = root.join(PRODUCTION_ORDER_PROP);

        Subquery<Long> maxIdSubquery = criteriaQuery.subquery(Long.class);
        Root<CounterRecordEntity> maxIdRoot = maxIdSubquery.from(CounterRecordEntity.class);
        maxIdSubquery.select(criteriaBuilder.max(maxIdRoot.get(ID_PROP)))
                .where(criteriaBuilder.equal(maxIdRoot.get(EQUIPMENT_OUTPUT_PROP), equipmentOutputJoin),
                        criteriaBuilder.equal(maxIdRoot.get(PRODUCTION_ORDER_PROP), productionOrderJoin));

        Subquery<Long> maxIdPerOutputSubquery = criteriaQuery.subquery(Long.class);
        Root<CounterRecordEntity> maxIdPerOutputRoot = maxIdPerOutputSubquery.from(CounterRecordEntity.class);
        maxIdPerOutputSubquery.select(criteriaBuilder.max(maxIdPerOutputRoot.get(ID_PROP)))
                .where(criteriaBuilder.equal(maxIdPerOutputRoot.get(EQUIPMENT_OUTPUT_PROP), equipmentOutputJoin),
                        criteriaBuilder.equal(maxIdPerOutputRoot.get(PRODUCTION_ORDER_PROP), productionOrderJoin));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get(ID_PROP), maxIdSubquery.getSelection()));
        predicates.add(criteriaBuilder.equal(root.get(ID_PROP), maxIdPerOutputSubquery.getSelection()));
        addPredicates(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        addSortOrders(filterDto.getSort(), orders, criteriaBuilder, root);
        orders.add(criteriaBuilder.desc(productionOrderJoin.get(ID_PROP)));
        orders.add(criteriaBuilder.asc(equipmentOutputJoin.get(ID_PROP)));

        criteriaQuery.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake() + 1)
                .getResultList();
    }

    private void addPredicates(CounterRecordFilterDto filterDto, List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<CounterRecordEntity> counterRecordRoot) {
        for (CounterRecordSearchDto search : filterDto.getSearch()) {
            Predicate predicate;
            switch (search.getId()) {
                case COMPUTED_VALUE_PROP ->
                        predicate = criteriaBuilder.equal(counterRecordRoot.get(COMPUTED_VALUE_PROP), search.getValue());
                case DATE_START_FILTER_FIELD -> {
                    ZonedDateTime dateStart = ZonedDateTime.parse(search.getValue());
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateStart);
                }
                case DATE_END_FILTER_FIELD -> {
                    ZonedDateTime dateEnd = ZonedDateTime.parse(search.getValue());
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_PROP), dateEnd);
                }
                default -> {
                    Path<?> path = getPath(counterRecordRoot, search.getId());
                    String value = SQL_WILDCARD + search.getValue().toUpperCase() + SQL_WILDCARD;
                    predicate = createLikePredicate(path, value, criteriaBuilder);
                }
            }
            predicates.add(predicate);
        }
    }

    private Predicate createLikePredicate(Path<?> path, String value, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), value);
    }

    private void addSortOrders(CounterRecordSortDto[] sortList, List<Order> orders, CriteriaBuilder criteriaBuilder, Root<CounterRecordEntity> counterRecordRoot) {
        for (CounterRecordSortDto sort : sortList) {
            Order order = sort.isDesc() ?
                    criteriaBuilder.desc(getPath(counterRecordRoot, sort.getId())) :
                    criteriaBuilder.asc(getPath(counterRecordRoot, sort.getId()));
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
}
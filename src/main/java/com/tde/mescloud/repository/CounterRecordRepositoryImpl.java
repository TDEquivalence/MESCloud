package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.CounterRecordSearchDto;
import com.tde.mescloud.model.dto.CounterRecordSortDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.utility.SpringContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

    private static final String ID_FIELD = "id";
    private static final String EQUIPMENT_OUTPUT_FIELD = "equipmentOutput";
    private static final String PRODUCTION_ORDER_FIELD = "productionOrder";
    private static final String COMPUTED_VALUE_FIELD = "computedValue";
    private static final String REGISTERED_AT_FIELD = "registeredAt";
    private static final String EQUIPMENT_ALIAS_FIELD = "equipmentAlias";
    private static final String COUNTING_EQUIPMENT_FIELD = "countingEquipment";
    private static final String PRODUCTION_ORDER_CODE_FIELD = "productionOrderCode";

    public List<CounterRecordEntity> findLastPerProductionOrder(CounterRecordFilterDto filterDto) {
        EntityManager entityManager = SpringContext.getEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> criteriaQuery = criteriaBuilder.createQuery(CounterRecordEntity.class);

        Root<CounterRecordEntity> root = criteriaQuery.from(CounterRecordEntity.class);
        Join<CounterRecordEntity, EquipmentOutputEntity> equipmentOutputJoin = root.join(EQUIPMENT_OUTPUT_FIELD);
        Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin = root.join(PRODUCTION_ORDER_FIELD);

        Subquery<Long> maxIdSubquery = criteriaQuery.subquery(Long.class);
        Root<CounterRecordEntity> maxIdRoot = maxIdSubquery.from(CounterRecordEntity.class);
        maxIdSubquery.select(criteriaBuilder.max(maxIdRoot.get(ID_FIELD)))
                .where(
                        criteriaBuilder.equal(maxIdRoot.get(EQUIPMENT_OUTPUT_FIELD), equipmentOutputJoin),
                        criteriaBuilder.equal(maxIdRoot.get(PRODUCTION_ORDER_FIELD), productionOrderJoin)
                );

        Subquery<Long> maxIdPerOutputSubquery = criteriaQuery.subquery(Long.class);
        Root<CounterRecordEntity> maxIdPerOutputRoot = maxIdPerOutputSubquery.from(CounterRecordEntity.class);
        maxIdPerOutputSubquery.select(criteriaBuilder.max(maxIdPerOutputRoot.get(ID_FIELD)))
                .where(criteriaBuilder.equal(maxIdPerOutputRoot.get(EQUIPMENT_OUTPUT_FIELD), equipmentOutputJoin));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get(ID_FIELD), maxIdSubquery.getSelection()));
        predicates.add(criteriaBuilder.equal(root.get(ID_FIELD), maxIdPerOutputSubquery.getSelection()));
        addPredicates(filterDto, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        addSortOrders(filterDto.getSort(), orders, criteriaBuilder, root);

        criteriaQuery.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<CounterRecordEntity> findByCriteria(CounterRecordFilterDto filterDto) {
        EntityManager entityManager = SpringContext.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> counterRecordCriteriaQuery = criteriaBuilder.createQuery(CounterRecordEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        Root<CounterRecordEntity> counterRecordRoot = counterRecordCriteriaQuery.from(CounterRecordEntity.class);

        addPredicates(filterDto, predicates, criteriaBuilder, counterRecordRoot);
        addSortOrders(filterDto.getSort(), orders, criteriaBuilder, counterRecordRoot);

        Order orderByDate = criteriaBuilder.desc(getPath(counterRecordRoot, REGISTERED_AT_FIELD));
        orders.add(orderByDate);

        counterRecordCriteriaQuery
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        TypedQuery<CounterRecordEntity> query = entityManager
                .createQuery(counterRecordCriteriaQuery)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake() + 1);

        return query.getResultList();
    }

    private void addPredicates(CounterRecordFilterDto filterDto, List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<CounterRecordEntity> counterRecordRoot) {
        for (CounterRecordSearchDto search : filterDto.getSearch()) {
            Predicate predicate;
            switch (search.getId()) {
                case COMPUTED_VALUE_FIELD:
                    predicate = criteriaBuilder.equal(counterRecordRoot.get(COMPUTED_VALUE_FIELD), search.getValue());
                    break;
                case "dateStart":
                    ZonedDateTime dateStart = ZonedDateTime.parse(search.getValue());
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_FIELD), dateStart);
                    break;
                case "dateEnd":
                    ZonedDateTime dateEnd = ZonedDateTime.parse(search.getValue());
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get(REGISTERED_AT_FIELD), dateEnd);
                    break;
                default:
                    Path<?> path = getPath(counterRecordRoot, search.getId());
                    String value = "%" + search.getValue().toUpperCase() + "%";
                    predicate = createLikePredicate(path, value, criteriaBuilder);
                    break;
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
            case EQUIPMENT_ALIAS_FIELD:
                Join<CounterRecordEntity, EquipmentOutputEntity> equipmentOutputJoin =
                        counterRecordRoot.join(EQUIPMENT_OUTPUT_FIELD);
                Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                        equipmentOutputJoin.join(COUNTING_EQUIPMENT_FIELD);
                return countingEquipmentJoin.get(EQUIPMENT_ALIAS_FIELD);
            case PRODUCTION_ORDER_CODE_FIELD:
                Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin =
                        counterRecordRoot.join(PRODUCTION_ORDER_FIELD);
                return productionOrderJoin.get(PRODUCTION_ORDER_CODE_FIELD);
            default:
                return counterRecordRoot.get(property);
        }
    }
}
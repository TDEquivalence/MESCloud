package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ProductionOrderRepositoryImpl {

    private static final String COMPLETED_AT = "completedAt";
    private static final String PRODUCTION_ORDER_CODE = "code";
    private static final String COMPOSED_PRODUCTION_ORDER = "composedProductionOrder";
    private static final String IS_COMPLETED = "isCompleted";
    private static final String PROP_ID = "id";

    private final EntityManager entityManager;

    public List<ProductionOrderSummaryEntity> findCompleted(boolean withoutComposed, Filter filter, Timestamp startDate, Timestamp endDate) {
        String productionOrderCode = null;
        Integer skip = null;
        Integer take = null;

        if (filter != null) {
            productionOrderCode = filter.getSearch().getValue(Filter.Property.PRODUCTION_ORDER_CODE);
            skip = filter.getSkip();
            take = filter.getTake();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderSummaryEntity> query = criteriaBuilder.createQuery(ProductionOrderSummaryEntity.class);
        Root<ProductionOrderSummaryEntity> root = query.from(ProductionOrderSummaryEntity.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (withoutComposed) {
            // Add predicate to filter by composedProductionOrder == null
            Predicate composedProductionOrderIsNull = criteriaBuilder.isNull(root.get(COMPOSED_PRODUCTION_ORDER));
            predicates.add(composedProductionOrderIsNull);
        }

        if (startDate != null) {
            Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get(COMPLETED_AT), startDate);
            predicates.add(startDatePredicate);
        }

        if (endDate != null) {
            Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get(COMPLETED_AT), endDate);
            predicates.add(endDatePredicate);
        }

        Predicate isCompletedPredicate = criteriaBuilder.isTrue(root.get(IS_COMPLETED));
        predicates.add(isCompletedPredicate);

        if (productionOrderCode != null && !productionOrderCode.isEmpty()) {
            Predicate productionOrderCodePredicate = criteriaBuilder.like(root.get(PRODUCTION_ORDER_CODE), "%" + productionOrderCode + "%");
            predicates.add(productionOrderCodePredicate);
        }
        query.where(predicates.toArray(new Predicate[0]));

        List<Order> orders = new ArrayList<>();
        Order newestOrder = criteriaBuilder.desc(root.get(PROP_ID));
        orders.add(newestOrder);

        query.orderBy(orders);

        TypedQuery<ProductionOrderSummaryEntity> typedQuery = entityManager.createQuery(query);

        if (skip != null) {
            typedQuery.setFirstResult(skip);
        }

        if (take != null) {
            typedQuery.setMaxResults(take);
        }

        return typedQuery.getResultList();
    }


    public List<ProductionOrderSummaryEntity> findProductionOrderSummaryByComposedId(Long composedProductionOrderId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderSummaryEntity> query = criteriaBuilder.createQuery(ProductionOrderSummaryEntity.class);
        Root<ProductionOrderSummaryEntity> root = query.from(ProductionOrderSummaryEntity.class);
        Join<ProductionOrderSummaryEntity, ComposedProductionOrderEntity> joinComposedProductionOrder = root.join(COMPOSED_PRODUCTION_ORDER,
                JoinType.LEFT);

        query.select(root)
                .where(criteriaBuilder.equal(joinComposedProductionOrder.get("id"), composedProductionOrderId));

        return entityManager.createQuery(query).getResultList();
    }
}

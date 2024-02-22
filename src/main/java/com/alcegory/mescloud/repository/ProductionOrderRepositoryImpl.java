package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ProductionOrderRepositoryImpl {
    
    private static final String COMPLETED_AT = "completeddAt";
    private static final String COMPOSED_PRODUCTION_ORDER = "composedProductionOrder";
    private static final String IS_COMPLETED = "isCompleted";
    private static final String PROP_ID = "id";

    private final EntityManager entityManager;

    public List<ProductionOrderSummaryEntity> findCompleted(Timestamp startDate, Timestamp endDate, boolean withoutComposed) {
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

        // Add predicate for isCompleted = true
        Predicate isCompletedPredicate = criteriaBuilder.isTrue(root.get(IS_COMPLETED));
        predicates.add(isCompletedPredicate);

        query.where(predicates.toArray(new Predicate[0]));

        List<Order> orders = new ArrayList<>();
        Order newestOrder = criteriaBuilder.desc(root.get(PROP_ID));
        orders.add(newestOrder);

        query.orderBy(orders);

        return entityManager.createQuery(query).getResultList();
    }

    public List<ProductionOrderSummaryEntity> findProductionOrderSummaryByComposedId(Long composedProductionOrderId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderSummaryEntity> query = criteriaBuilder.createQuery(ProductionOrderSummaryEntity.class);
        Root<ProductionOrderSummaryEntity> root = query.from(ProductionOrderSummaryEntity.class);
        Join<ProductionOrderSummaryEntity, ComposedProductionOrderEntity> joinComposedProductionOrder = root.join("composedProductionOrder", JoinType.LEFT);

        query.select(root)
                .where(criteriaBuilder.equal(joinComposedProductionOrder.get("id"), composedProductionOrderId));

        return entityManager.createQuery(query).getResultList();
    }
}

package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ProductionOrderRepositoryImpl {

    private static final String PROP_ID = "id";

    private final EntityManager entityManager;

    public List<ProductionOrderSummaryEntity> findCompletedWithoutComposed() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderSummaryEntity> query = criteriaBuilder.createQuery(ProductionOrderSummaryEntity.class);
        Root<ProductionOrderSummaryEntity> root = query.from(ProductionOrderSummaryEntity.class);
        query.select(root);

        List<Order> orders = new ArrayList<>();
        Order newestOrder = criteriaBuilder.desc(root.get(PROP_ID));
        orders.add(newestOrder);

        query.select(root)
                .orderBy(orders);

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

package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ProductionOrderSummaryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class ProductionOrderRepositoryImpl {

    private EntityManager entityManager;

    public List<ProductionOrderSummaryEntity> findCompletedAndUnassociated() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderSummaryEntity> query = criteriaBuilder.createQuery(ProductionOrderSummaryEntity.class);
        Root<ProductionOrderSummaryEntity> root = query.from(ProductionOrderSummaryEntity.class);
        query.select(root);

        TypedQuery<ProductionOrderSummaryEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}

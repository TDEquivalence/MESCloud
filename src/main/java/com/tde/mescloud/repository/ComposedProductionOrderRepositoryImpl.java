package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.BatchEntity;
import com.tde.mescloud.model.entity.ComposedSummaryEntity;
import com.tde.mescloud.model.entity.HitEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ComposedProductionOrderRepositoryImpl {

    private EntityManager entityManager;

    public List<ComposedSummaryEntity> findCompleted() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(hasAssociatedBatchPredicate(query, root));

        List<Order> orders = new ArrayList<>();

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query).getResultList();
    }

    private Predicate hasAssociatedBatchPredicate(CriteriaQuery rootQuery, Root root) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Subquery<Integer> subquery = rootQuery.subquery(Integer.class);
        Root<BatchEntity> subRoot = subquery.from(BatchEntity.class);
        subquery.select(subRoot.get("composed").get("id"));

        return root.get("id").in(subquery);
    }

    public List<ComposedSummaryEntity> findSummarized(boolean withHits) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!withHits) {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<HitEntity> subRoot = subquery.from(HitEntity.class);
            subquery.select(subRoot.get("sample").get("composedProductionOrder").get("id"));

            Predicate noHitsPredicate = criteriaBuilder.not(root.get("id").in(subquery));
            predicates.add(noHitsPredicate);
        } else {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<HitEntity> subRoot = subquery.from(HitEntity.class);
            subquery.select(subRoot.get("sample").get("composedProductionOrder").get("id"));

            Predicate hitsPredicate = root.get("id").in(subquery);
            predicates.add(hitsPredicate);
        }

        List<Order> orders = new ArrayList<>();

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query).getResultList();
    }
}

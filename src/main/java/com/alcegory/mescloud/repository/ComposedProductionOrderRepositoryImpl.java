package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.BatchEntity;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.HitEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ComposedProductionOrderRepositoryImpl {

    private static final String PROP_ID = "id";
    private static final String PROP_SAMPLE = "sample";
    private static final String PROP_COMPOSED_PO = "composedProductionOrder";

    private final EntityManager entityManager;


    public List<ComposedSummaryEntity> findCompleted() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(hasAssociatedBatchPredicate(query, root));

        List<Order> orders = new ArrayList<>();
        Order newestOrder = criteriaBuilder.desc(root.get(PROP_ID));
        orders.add(newestOrder);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query).getResultList();
    }

    private Predicate hasAssociatedBatchPredicate(CriteriaQuery<ComposedSummaryEntity> rootQuery,
                                                  Root<ComposedSummaryEntity> root) {

        Subquery<Integer> subquery = rootQuery.subquery(Integer.class);
        Root<BatchEntity> subRoot = subquery.from(BatchEntity.class);
        subquery.select(subRoot.get(PROP_COMPOSED_PO).get(PROP_ID));

        return root.get(PROP_ID).in(subquery);
    }

    public List<ComposedSummaryEntity> getOpenComposedSummaries(boolean withHits) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!withHits) {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<HitEntity> subRoot = subquery.from(HitEntity.class);
            subquery.select(subRoot.get(PROP_SAMPLE).get(PROP_COMPOSED_PO).get(PROP_ID));

            Predicate noHitsPredicate = criteriaBuilder.not(root.get(PROP_ID).in(subquery));
            predicates.add(noHitsPredicate);
        } else {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<HitEntity> subRoot = subquery.from(HitEntity.class);
            subquery.select(subRoot.get(PROP_SAMPLE).get(PROP_COMPOSED_PO).get(PROP_ID));

            Predicate hitsPredicate = root.get(PROP_ID).in(subquery);
            predicates.add(hitsPredicate);
        }

        Subquery<Integer> batchSubquery = query.subquery(Integer.class);
        Root<BatchEntity> batchRoot = batchSubquery.from(BatchEntity.class);
        batchSubquery.select(batchRoot.get(PROP_COMPOSED_PO).get(PROP_ID));

        Predicate noBatchPredicate = criteriaBuilder.not(root.get(PROP_ID).in(batchSubquery));
        predicates.add(noBatchPredicate);

        List<Order> orders = new ArrayList<>();
        Order newestOrder = criteriaBuilder.desc(root.get(PROP_ID));
        orders.add(newestOrder);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query).getResultList();
    }
}

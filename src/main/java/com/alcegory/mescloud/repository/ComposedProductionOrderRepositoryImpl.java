package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.BatchEntity;
import com.alcegory.mescloud.model.entity.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.HitEntity;
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
public class ComposedProductionOrderRepositoryImpl {

    private static final String PROP_ID = "id";
    private static final String PROP_SAMPLE = "sample";
    private static final String PROP_COMPOSED_PO = "composedProductionOrder";
    private static final String CREATED_AT = "createdAt";
    private static final String APPROVED_AT = "approvedAt";

    private final EntityManager entityManager;


    public List<ComposedSummaryEntity> findCompleted(Timestamp startDate, Timestamp endDate) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(hasAssociatedBatchPredicate(query, root));

        // Predicates for startDate and endDate
        if (startDate != null) {
            Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get(APPROVED_AT), startDate);
            predicates.add(startDatePredicate);
        }
        if (endDate != null) {
            Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get(APPROVED_AT), endDate);
            predicates.add(endDatePredicate);
        }
        List<Order> orders = new ArrayList<>();
        Order approvedAtDescOrder = criteriaBuilder.desc(root.get(APPROVED_AT));
        orders.add(approvedAtDescOrder);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query).getResultList();
    }

    public List<ComposedSummaryEntity> findAllComposed(Timestamp startDate, Timestamp endDate) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = builder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        query.distinct(true);

        // Adding predicates to filter by startDate and endDate
        Predicate datePredicate = builder.between(root.get(CREATED_AT), startDate, endDate);
        query.where(datePredicate);

        // Execute query
        TypedQuery<ComposedSummaryEntity> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    private Predicate hasAssociatedBatchPredicate(CriteriaQuery<ComposedSummaryEntity> rootQuery,
                                                  Root<ComposedSummaryEntity> root) {

        Subquery<Integer> subquery = rootQuery.subquery(Integer.class);
        Root<BatchEntity> subRoot = subquery.from(BatchEntity.class);
        subquery.select(subRoot.get(PROP_COMPOSED_PO).get(PROP_ID));

        return root.get(PROP_ID).in(subquery);
    }

    public List<ComposedSummaryEntity> getOpenComposedSummaries(boolean withHits, Timestamp startDate, Timestamp endDate) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        // Predicate for withHits condition
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

        // Predicates for startDate and endDate
        if (startDate != null) {
            Predicate startDatePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get(CREATED_AT), startDate);
            predicates.add(startDatePredicate);
        }
        if (endDate != null) {
            Predicate endDatePredicate = criteriaBuilder.lessThanOrEqualTo(root.get(CREATED_AT), endDate);
            predicates.add(endDatePredicate);
        }

        // Predicate to exclude instances where approvedAt is not null
        Predicate approvedAtIsNullPredicate = criteriaBuilder.isNull(root.get(APPROVED_AT));
        predicates.add(approvedAtIsNullPredicate);

        // Constructing the final query
        query.where(predicates.toArray(new Predicate[0]));

        // Ordering by created_at
        query.orderBy(criteriaBuilder.desc(root.get(CREATED_AT)));
        // Execute the query and return results
        return entityManager.createQuery(query).getResultList();
    }
}

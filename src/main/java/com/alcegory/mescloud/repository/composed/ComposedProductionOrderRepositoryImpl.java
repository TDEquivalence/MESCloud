package com.alcegory.mescloud.repository.composed;

import com.alcegory.mescloud.model.entity.BatchEntity;
import com.alcegory.mescloud.model.entity.production.ComposedSummaryEntity;
import com.alcegory.mescloud.model.entity.HitEntity;
import com.alcegory.mescloud.model.filter.Filter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.model.filter.Filter.Property.END_DATE;
import static com.alcegory.mescloud.model.filter.Filter.Property.START_DATE;

@AllArgsConstructor
@Repository
public class ComposedProductionOrderRepositoryImpl {

    private static final String PROP_ID = "id";
    private static final String PROP_SAMPLE = "sample";
    private static final String PROP_COMPOSED_PO = "composedProductionOrder";
    private static final String CREATED_AT = "createdAt";
    private static final String APPROVED_AT = "approvedAt";
    private static final String INSERT_HIT_AT = "hitInsertedAt";

    private final EntityManager entityManager;

    public List<ComposedSummaryEntity> findCompleted(Filter filter, Long composedId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        // Construct predicates
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(hasAssociatedBatchPredicate(query, root));
        predicates.addAll(getDatePredicates(criteriaBuilder, root, filter));

        if (composedId != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), composedId));
        }
        // Construct ordering
        List<Order> orders = new ArrayList<>();
        orders.add(criteriaBuilder.desc(root.get(APPROVED_AT)));

        // Construct and execute the query
        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(orders);

        return entityManager.createQuery(query)
                .setFirstResult(filter.getSkip())
                .setMaxResults(filter.getTake())
                .getResultList();
    }

    public List<ComposedSummaryEntity> findAllComposed(Timestamp startDate, Timestamp endDate) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = builder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        query.distinct(true);

        Predicate createdAtPredicate = builder.between(root.get(CREATED_AT), startDate, endDate);
        Predicate insertHitAtPredicate = builder.between(root.get(INSERT_HIT_AT), startDate, endDate);
        Predicate iApprovedAtPredicate = builder.between(root.get(APPROVED_AT), startDate, endDate);

        Predicate combinedPredicate = builder.or(createdAtPredicate, insertHitAtPredicate, iApprovedAtPredicate);

        query.where(combinedPredicate);

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

    public List<ComposedSummaryEntity> getOpenComposedSummaries(boolean withHits, Filter filter, Long composedId) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        // Predicate for withHits condition
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<HitEntity> subRoot = subquery.from(HitEntity.class);
        subquery.select(subRoot.get(PROP_SAMPLE).get(PROP_COMPOSED_PO).get(PROP_ID));

        Predicate hitsPredicate = withHits ? root.get(PROP_ID).in(subquery) : criteriaBuilder.not(root.get(PROP_ID).in(subquery));
        predicates.add(hitsPredicate);

        // Handle filter
        if (filter != null) {
            Optional<Timestamp> startDateOpt = Optional.ofNullable(filter.getSearch().getTimestampValue(Filter.Property.START_DATE));
            Optional<Timestamp> endDateOpt = Optional.ofNullable(filter.getSearch().getTimestampValue(Filter.Property.END_DATE));

            startDateOpt.ifPresent(startDate -> predicates.add(
                    withHits ? criteriaBuilder.greaterThanOrEqualTo(root.get(INSERT_HIT_AT), startDate)
                            : criteriaBuilder.greaterThanOrEqualTo(root.get(CREATED_AT), startDate)
            ));
            endDateOpt.ifPresent(endDate -> predicates.add(
                    withHits ? criteriaBuilder.lessThanOrEqualTo(root.get(INSERT_HIT_AT), endDate)
                            : criteriaBuilder.lessThanOrEqualTo(root.get(CREATED_AT), endDate)
            ));
        }

        // Exclude instances where approvedAt is not null
        predicates.add(criteriaBuilder.isNull(root.get(APPROVED_AT)));

        // Add predicate for composedId if it's not null
        if (composedId != null) {
            predicates.add(criteriaBuilder.equal(root.get(PROP_ID), composedId));
        }

        // Constructing the final query
        query.where(predicates.toArray(new Predicate[0]));

        // Ordering by created_at or insert_hit_at based on withHits condition
        query.orderBy(withHits ? criteriaBuilder.desc(root.get(INSERT_HIT_AT)) : criteriaBuilder.desc(root.get(CREATED_AT)));

        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        // Execute the query and return results
        return entityManager.createQuery(query)
                .setFirstResult(filter.getSkip())
                .setMaxResults(filter.getTake())
                .getResultList();
    }

    private List<Predicate> getDatePredicates(CriteriaBuilder criteriaBuilder, Root<ComposedSummaryEntity> root, Filter filter) {
        List<Predicate> datePredicates = new ArrayList<>();
        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);

        if (startDate != null) {
            datePredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(APPROVED_AT), startDate));
        }
        if (endDate != null) {
            datePredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(APPROVED_AT), endDate));
        }

        return datePredicates;
    }
}

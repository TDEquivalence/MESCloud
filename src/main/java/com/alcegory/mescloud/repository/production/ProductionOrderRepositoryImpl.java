package com.alcegory.mescloud.repository.production;

import com.alcegory.mescloud.model.entity.composed.ComposedProductionOrderEntity;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
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

@AllArgsConstructor
@Repository
public class ProductionOrderRepositoryImpl {

    private static final String COMPLETED_AT = "completedAt";
    private static final String PRODUCTION_ORDER_CODE = "code";
    private static final String COMPOSED_PRODUCTION_ORDER = "composedProductionOrder";
    private static final String IS_COMPLETED = "isCompleted";
    private static final String ID_PROP = "id";

    private static final String EQUIPMENT = "equipment";
    private static final String SECTION = "section";

    private final EntityManager entityManager;

    public List<ProductionOrderEntity> findCompleted(long sectionId, boolean withoutComposed, Filter filter, Timestamp startDate, Timestamp endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<ProductionOrderEntity> root = idQuery.from(ProductionOrderEntity.class);


        Join<ProductionOrderEntity, CountingEquipmentEntity> equipmentJoin = root.join(EQUIPMENT);

        Predicate sectionPredicate = cb.equal(equipmentJoin.get(SECTION).get(ID_PROP), sectionId);
        Predicate[] predicates = buildPredicates(cb, root, withoutComposed, startDate, endDate, filter);
        Predicate finalPredicate = cb.and(sectionPredicate, cb.and(predicates));

        idQuery.where(finalPredicate);
        idQuery.select(root.get(ID_PROP));
        TypedQuery<Long> idTypedQuery = entityManager.createQuery(idQuery);

        if (filter != null) {
            int skip = filter.getSkip();
            int take = filter.getTake();
            idTypedQuery.setFirstResult(skip);
            idTypedQuery.setMaxResults(take);
        }

        List<Long> ids = idTypedQuery.getResultList();

        CriteriaQuery<ProductionOrderEntity> mainQuery = cb.createQuery(ProductionOrderEntity.class);
        Root<ProductionOrderEntity> mainRoot = mainQuery.from(ProductionOrderEntity.class);

        mainRoot.fetch(EQUIPMENT, JoinType.LEFT);

        mainQuery.where(mainRoot.get(ID_PROP).in(ids));
        mainQuery.orderBy(cb.desc(mainRoot.get(ID_PROP)));

        TypedQuery<ProductionOrderEntity> typedQuery = entityManager.createQuery(mainQuery);

        return typedQuery.getResultList();
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<ProductionOrderEntity> root, boolean withoutComposed,
                                        Timestamp startDate, Timestamp endDate, Filter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (withoutComposed) {
            predicates.add(cb.isNull(root.get(COMPOSED_PRODUCTION_ORDER)));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(COMPLETED_AT), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(COMPLETED_AT), endDate));
        }
        predicates.add(cb.isTrue(root.get(IS_COMPLETED)));

        if (filter != null) {
            String productionOrderCode = filter.getSearch().getValue(Filter.Property.PRODUCTION_ORDER_CODE);
            if (productionOrderCode != null && !productionOrderCode.isEmpty()) {
                predicates.add(cb.like(root.get(PRODUCTION_ORDER_CODE), "%" + productionOrderCode + "%"));
            }
        }

        return predicates.toArray(new Predicate[0]);
    }


    public List<ProductionOrderEntity> findProductionOrderSummaryByComposedId(Long composedProductionOrderId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderEntity> query = cb.createQuery(ProductionOrderEntity.class);
        Root<ProductionOrderEntity> root = query.from(ProductionOrderEntity.class);
        Join<ProductionOrderEntity, ComposedProductionOrderEntity> joinComposedProductionOrder = root.join(COMPOSED_PRODUCTION_ORDER,
                JoinType.LEFT);

        query.select(root)
                .where(cb.equal(joinComposedProductionOrder.get(ID_PROP), composedProductionOrderId));

        return entityManager.createQuery(query).getResultList();
    }

    public Optional<ProductionOrderEntity> findLastByEquipmentId(long equipmentId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductionOrderEntity> query = criteriaBuilder.createQuery(ProductionOrderEntity.class);
        Root<ProductionOrderEntity> root = query.from(ProductionOrderEntity.class);

        Join<ProductionOrderEntity, CountingEquipmentEntity> equipmentJoin = root.join(EQUIPMENT);

        query.select(root)
                .where(criteriaBuilder.equal(equipmentJoin.get(ID_PROP), equipmentId))
                .orderBy(criteriaBuilder.desc(root.get(ID_PROP)));

        return entityManager.createQuery(query)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }
}

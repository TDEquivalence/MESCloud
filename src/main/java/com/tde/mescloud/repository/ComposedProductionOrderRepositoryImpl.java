package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ComposedSummaryEntity;
import com.tde.mescloud.model.entity.HitEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class ComposedProductionOrderRepositoryImpl {

    private EntityManager entityManager;

    public List<ComposedSummaryEntity> findAllWithoutHits() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComposedSummaryEntity> query = criteriaBuilder.createQuery(ComposedSummaryEntity.class);
        Root<ComposedSummaryEntity> root = query.from(ComposedSummaryEntity.class);

        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<HitEntity> subRoot = subquery.from(HitEntity.class);
        subquery.select(subRoot.get("sample").get("id"));

        Predicate noHitsPredicate = criteriaBuilder.not(root.get("id").in(subquery));

        query.select(root);
        query.where(noHitsPredicate);

        return entityManager.createQuery(query).getResultList();
    }
}

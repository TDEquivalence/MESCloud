package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.utility.SpringContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CountingEquipmentRepository extends CrudRepository<CountingEquipmentEntity, Long> {

    default List<CountingEquipmentEntity> findByProductionOrderStatus(boolean forActiveProductionOrder) {

        EntityManager entityManager = SpringContext.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountingEquipmentEntity> criteriaQuery = criteriaBuilder.createQuery(CountingEquipmentEntity.class);
        Root<CountingEquipmentEntity> root = criteriaQuery.from(CountingEquipmentEntity.class);

        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<ProductionOrderEntity> productionOrderRoot = subquery.from(ProductionOrderEntity.class);
        subquery.select(productionOrderRoot.get("equipment").get("id"));

        Predicate statusPredicate = forActiveProductionOrder ?
                criteriaBuilder.isFalse(productionOrderRoot.get("isCompleted")) :
                criteriaBuilder.isTrue(productionOrderRoot.get("isCompleted"));

        subquery.where(statusPredicate);
        Predicate isActivePredicate = criteriaBuilder.in(root.get("id")).value(subquery);
        criteriaQuery.where(isActivePredicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}

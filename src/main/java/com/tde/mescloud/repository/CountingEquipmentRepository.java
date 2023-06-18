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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountingEquipmentEntity> query = cb.createQuery(CountingEquipmentEntity.class);

        Root<CountingEquipmentEntity> countingEquipmentRoot = query.from(CountingEquipmentEntity.class);
        Join<CountingEquipmentEntity, ProductionOrderEntity> productionOrderJoin = countingEquipmentRoot.join("productionOrders");

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<ProductionOrderEntity> subqueryRoot = subquery.from(ProductionOrderEntity.class);
        subquery.select(cb.max(subqueryRoot.get("id")))
                .where(cb.equal(subqueryRoot.get("equipment").get("id"), countingEquipmentRoot.get("id")));

        query.select(countingEquipmentRoot)
                .where(cb.and(
                        cb.equal(productionOrderJoin.get("id"), subquery)),
                        cb.equal(productionOrderJoin.get("isCompleted"), !forActiveProductionOrder)
                );

        List<CountingEquipmentEntity> results = entityManager.createQuery(query).getResultList();
        return results;
    }
}

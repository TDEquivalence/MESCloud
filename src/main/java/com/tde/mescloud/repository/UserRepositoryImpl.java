package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.UserWinnow;
import com.tde.mescloud.model.entity.UserEntity;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl extends AbstractWinnowRepository {


    public List<UserEntity> findAll(UserWinnow winnow) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        addPredicates(winnow, predicates, criteriaBuilder, root);

        List<Order> orders = new ArrayList<>();
        addSortOrders(winnow, orders, criteriaBuilder, root);

        query.select(root)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        return entityManager.createQuery(query)
                .getResultList();
    }
}

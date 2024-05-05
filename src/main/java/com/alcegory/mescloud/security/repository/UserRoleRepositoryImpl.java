package com.alcegory.mescloud.security.repository;

import com.alcegory.mescloud.security.model.UserRoleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class UserRoleRepositoryImpl {

    private final EntityManager entityManager;

    public List<UserRoleEntity> findByUserId(Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> root = cq.from(UserRoleEntity.class);

        cq.select(root).where(cb.equal(root.get("userId"), userId));

        return entityManager.createQuery(cq).getResultList();
    }

    public UserRoleEntity findUserRoleByUserAndSection(Long userId, Long sectionId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRoleEntity> cq = cb.createQuery(UserRoleEntity.class);
        Root<UserRoleEntity> root = cq.from(UserRoleEntity.class);

        cq.select(root).where(cb.and(
                cb.equal(root.get("userId"), userId),
                cb.equal(root.get("sectionId"), sectionId)
        ));

        return entityManager.createQuery(cq).getSingleResult();
    }
}

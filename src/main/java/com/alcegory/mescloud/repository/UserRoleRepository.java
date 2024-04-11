package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.security.model.UserRoleEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleEntity.UserRoleId> {

    List<UserRoleEntity> findByUserId(Long userId);

    UserRoleEntity findByUserIdAndSectionId(Long userId, Long sectionId);

    void deleteByUserId(Long userId);
}

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
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_role (user_id, role_id, section_id) VALUES (:userId, :roleId, :sectionId)", nativeQuery = true)
    void saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId, @Param("sectionId") Long sectionId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_role SET role_id = :roleId, section_id = :sectionId WHERE id = :userRoleId", nativeQuery = true)
    void updateUserRole(@Param("userRoleId") Long userRoleId, @Param("roleId") Long roleId, @Param("sectionId") Long sectionId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_role WHERE id = :userRoleId", nativeQuery = true)
    void deleteUserRoleById(@Param("userRoleId") Long userRoleId);

    @Query(value = "SELECT * FROM user_role WHERE user_id = :userId", nativeQuery = true)
    List<UserRoleEntity> findByUser(@Param("userId") Long userId);

    List<UserRoleEntity> findByUserId(Long userId);

    UserRoleEntity findUserRoleByUserAndSection(Long userId, Long sectionId);
}

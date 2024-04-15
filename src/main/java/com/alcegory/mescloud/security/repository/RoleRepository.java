package com.alcegory.mescloud.security.repository;

import com.alcegory.mescloud.security.model.SectionRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<SectionRoleEntity, Long> {

    @Query(value = "SELECT * FROM role WHERE name = :name", nativeQuery = true)
    Optional<SectionRoleEntity> findByName(String name);
}

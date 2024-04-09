package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.security.model.SectionRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<SectionRoleEntity, Long> {
}

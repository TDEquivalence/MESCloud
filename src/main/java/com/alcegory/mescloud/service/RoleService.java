package com.alcegory.mescloud.service;

import com.alcegory.mescloud.security.model.SectionRoleEntity;

import java.util.Optional;

public interface RoleService {

    SectionRoleEntity saveAndUpdate(SectionRoleEntity role);

    void delete(SectionRoleEntity role);

    Optional<SectionRoleEntity> findById(Long id);
}

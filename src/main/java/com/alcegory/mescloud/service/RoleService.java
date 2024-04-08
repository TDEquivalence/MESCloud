package com.alcegory.mescloud.service;

import com.alcegory.mescloud.security.model.RoleEntity;

import java.util.Optional;

public interface RoleService {

    RoleEntity saveAndUpdate(RoleEntity role);

    void delete(RoleEntity role);

    Optional<RoleEntity> findById(Long id);
}

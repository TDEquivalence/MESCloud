package com.alcegory.mescloud.security.service;

import com.alcegory.mescloud.security.repository.RoleRepository;
import com.alcegory.mescloud.security.model.SectionRole;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log
@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public SectionRoleEntity saveAndUpdate(SectionRoleEntity role) {
        return repository.save(role);
    }

    @Override
    public void delete(SectionRoleEntity role) {
        repository.delete(role);
    }

    @Override
    public Optional<SectionRoleEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<SectionRoleEntity> findByName(SectionRole sectionRole) {
        return repository.findByName(sectionRole.name());
    }
}

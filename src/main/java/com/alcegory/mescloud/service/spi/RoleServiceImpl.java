package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.RoleRepository;
import com.alcegory.mescloud.security.model.RoleEntity;
import com.alcegory.mescloud.service.RoleService;
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
    public RoleEntity saveAndUpdate(RoleEntity role) {
        return repository.save(role);
    }

    @Override
    public void delete(RoleEntity role) {
        repository.delete(role);
    }

    @Override
    public Optional<RoleEntity> findById(Long id) {
        return repository.findById(id);
    }
}

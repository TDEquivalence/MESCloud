package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.FactoryConverterImpl;
import com.tde.mescloud.model.dto.FactoryDto;
import com.tde.mescloud.model.entity.FactoryEntity;
import com.tde.mescloud.repository.FactoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FactoryServiceImpl implements FactoryService {

    private final FactoryRepository repository;
    private final FactoryConverterImpl converter;

    @Override
    public FactoryDto saveFactory(FactoryDto factoryDto) {
        validateFactoryDto(factoryDto);
        FactoryEntity entity = converter.convertToEntity(factoryDto);
        repository.save(entity);
        return converter.convertToDto(entity);
    }

    @Override
    public FactoryDto getFactoryById(Long id) {
        validateId(id);
        FactoryEntity entity = repository.findFactoryById(id);
        validateEntity(entity);
        return converter.convertToDto(entity);
    }

    @Override
    public FactoryDto getFactoryByName(String name) {
        validateName(name);
        FactoryEntity entity = repository.findByName(name);
        return converter.convertToDto(entity);
    }

    @Override
    public List<FactoryDto> getAllFactories() {
        List<FactoryEntity> factories = repository.findAll();
        return converter.convertToDto(factories);
    }

    @Override
    public void deleteFactoryById(Long id) {
        validateId(id);
        FactoryEntity entity = repository.findFactoryById(id);
        if (entity != null) {
            repository.delete(entity);
        } else {
            throw new EntityNotFoundException("Factory with ID " + id + " not found.");
        }
    }

    @Override
    public void deleteFactoryByName(String name) {
        validateName(name);
        FactoryEntity entity = repository.findByName(name);
        if (entity != null) {
            repository.delete(entity);
        } else {
            throw new EntityNotFoundException("Factory with name " + name + " not found.");
        }
    }

    private void validateFactoryDto(FactoryDto factoryDto) {
        if (factoryDto == null) {
            throw new IllegalArgumentException("FactoryDto cannot be null.");
        }
        // Validate other fields as needed
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid factory ID.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid factory name.");
        }
    }

    private void validateEntity(FactoryEntity factoryEntity) {
        if (factoryEntity == null) {
            throw new IllegalArgumentException("Invalid factory.");
        }
    }

}

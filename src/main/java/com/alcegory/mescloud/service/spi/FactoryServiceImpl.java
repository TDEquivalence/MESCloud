package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.FactoryRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.FactoryDto;
import com.alcegory.mescloud.model.entity.FactoryEntity;
import com.alcegory.mescloud.service.FactoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@ConfigurationProperties(prefix = "factory")
@Service
@AllArgsConstructor
public class FactoryServiceImpl implements FactoryService {

    private final FactoryRepository repository;
    private final GenericConverter<FactoryEntity, FactoryDto> converter;

    @Override
    public FactoryDto saveFactory(FactoryDto factoryDto) {
        validateFactoryDto(factoryDto);
        FactoryEntity entity = converter.toEntity(factoryDto, FactoryEntity.class);
        repository.save(entity);
        return converter.toDto(entity, FactoryDto.class);
    }

    @Override
    public FactoryDto getFactoryById(Long id) {
        validateId(id);
        FactoryEntity entity = repository.findFactoryById(id);
        validateEntity(entity);
        return converter.toDto(entity, FactoryDto.class);
    }

    @Override
    public FactoryDto getFactoryByName(String name) {
        validateName(name);
        FactoryEntity entity = repository.findByName(name);
        return converter.toDto(entity, FactoryDto.class);
    }

    @Override
    public List<FactoryDto> getAllFactories() {
        List<FactoryEntity> factories = repository.findAll();
        return converter.toDto(factories, FactoryDto.class);
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
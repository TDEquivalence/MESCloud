package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.FactoryConverter;
import com.tde.mescloud.model.dto.FactoryDto;
import com.tde.mescloud.model.entity.FactoryEntity;
import com.tde.mescloud.repository.FactoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FactoryServiceImpl implements FactoryService {

    private final FactoryRepository repository;
    private final FactoryConverter converter;

    @Override
    public FactoryDto saveFactory(FactoryDto factoryDto) {
        FactoryEntity entity = converter.convertToEntity(factoryDto);
        repository.save(entity);
        return converter.convertToDto(entity);
    }

    @Override
    public FactoryDto getFactoryById(Long id) {
        FactoryEntity entity = repository.findByFactoryId(id);
        return converter.convertToDto(entity);
    }

    @Override
    public FactoryDto getFactoryByName(String name) {
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
        FactoryEntity entity = repository.findByFactoryId(id);
        repository.delete(entity);
    }

    @Override
    public void deleteFactoryByName(String name) {
        FactoryEntity entity = repository.findByName(name);
        repository.delete(entity);
    }
}

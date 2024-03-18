package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.CompanyDto;
import com.alcegory.mescloud.model.entity.CompanyEntity;
import com.alcegory.mescloud.repository.CompanyRepository;
import com.alcegory.mescloud.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@ConfigurationProperties(prefix = "company")
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;
    private final GenericConverter<CompanyEntity, CompanyDto> converter;

    @Override
    public CompanyDto saveFactory(CompanyDto companyDto) {
        validateFactoryDto(companyDto);
        CompanyEntity entity = converter.toEntity(companyDto, CompanyEntity.class);
        repository.save(entity);
        return converter.toDto(entity, CompanyDto.class);
    }

    @Override
    public CompanyDto getFactoryById(Long id) {
        validateId(id);
        CompanyEntity entity = repository.findCompanyById(id);
        validateEntity(entity);
        return converter.toDto(entity, CompanyDto.class);
    }

    @Override
    public CompanyDto getFactoryByName(String name) {
        validateName(name);
        CompanyEntity entity = repository.findByName(name);
        return converter.toDto(entity, CompanyDto.class);
    }

    @Override
    public List<CompanyDto> getAllFactories() {
        List<CompanyEntity> factories = repository.findAll();
        return converter.toDto(factories, CompanyDto.class);
    }

    @Override
    public void deleteFactoryById(Long id) {
        validateId(id);
        CompanyEntity entity = repository.findCompanyById(id);
        if (entity != null) {
            repository.delete(entity);
        } else {
            throw new EntityNotFoundException("Factory with ID " + id + " not found.");
        }
    }

    @Override
    public void deleteFactoryByName(String name) {
        validateName(name);
        CompanyEntity entity = repository.findByName(name);
        if (entity != null) {
            repository.delete(entity);
        } else {
            throw new EntityNotFoundException("Factory with name " + name + " not found.");
        }
    }

    private void validateFactoryDto(CompanyDto companyDto) {
        if (companyDto == null) {
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

    private void validateEntity(CompanyEntity companyEntity) {
        if (companyEntity == null) {
            throw new IllegalArgumentException("Invalid factory.");
        }
    }
}
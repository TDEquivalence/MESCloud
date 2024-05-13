package com.alcegory.mescloud.service.company;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.CompanyDto;
import com.alcegory.mescloud.model.entity.CompanyEntity;
import com.alcegory.mescloud.repository.company.CompanyRepository;
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
    public CompanyDto saveCompany(CompanyDto companyDto) {
        validateFactoryDto(companyDto);
        CompanyEntity entity = converter.toEntity(companyDto, CompanyEntity.class);
        repository.save(entity);
        return converter.toDto(entity, CompanyDto.class);
    }

    @Override
    public CompanyDto getCompanyDtoById(Long id) {
        validateId(id);
        CompanyEntity entity = repository.findCompanyById(id);
        validateEntity(entity);
        return converter.toDto(entity, CompanyDto.class);
    }

    @Override
    public CompanyEntity getCompanyById(Long id) {
        validateId(id);
        return repository.findCompanyById(id);
    }

    @Override
    public CompanyDto getCompanyByName(String name) {
        validateName(name);
        CompanyEntity entity = repository.findByName(name);
        return converter.toDto(entity, CompanyDto.class);
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        List<CompanyEntity> factories = repository.findAll();
        return converter.toDto(factories, CompanyDto.class);
    }

    @Override
    public void deleteCompanyById(Long id) {
        validateId(id);
        CompanyEntity entity = repository.findCompanyById(id);
        if (entity != null) {
            repository.delete(entity);
        } else {
            throw new EntityNotFoundException("Factory with ID " + id + " not found.");
        }
    }

    @Override
    public void deleteCompanyByName(String name) {
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
            throw new IllegalArgumentException("CompanyDto cannot be null.");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid Company ID.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid Company name.");
        }
    }

    private void validateEntity(CompanyEntity companyEntity) {
        if (companyEntity == null) {
            throw new IllegalArgumentException("Invalid Company.");
        }
    }
}
package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

    CompanyDto saveFactory(CompanyDto companyDto);

    CompanyDto getFactoryById(Long id);

    CompanyDto getFactoryByName(String name);

    List<CompanyDto> getAllFactories();

    void deleteFactoryById(Long id);

    void deleteFactoryByName(String name);
}

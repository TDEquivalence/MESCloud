package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CompanyDto;
import com.alcegory.mescloud.model.entity.CompanyEntity;

import java.util.List;

public interface CompanyService {

    CompanyDto saveCompany(CompanyDto companyDto);

    CompanyEntity getCompanyById(Long id);

    CompanyDto getCompanyDtoById(Long id);

    CompanyDto getCompanyByName(String name);

    List<CompanyDto> getAllCompanies();

    void deleteCompanyById(Long id);

    void deleteCompanyByName(String name);
}

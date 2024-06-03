package com.alcegory.mescloud.service.company;

import com.alcegory.mescloud.model.dto.company.CompanyDto;
import com.alcegory.mescloud.model.entity.company.CompanyEntity;

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

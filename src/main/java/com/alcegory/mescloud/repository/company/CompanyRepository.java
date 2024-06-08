package com.alcegory.mescloud.repository.company;

import com.alcegory.mescloud.model.entity.company.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    CompanyEntity findCompanyById(Long id);

    CompanyEntity findByName(String name);
}

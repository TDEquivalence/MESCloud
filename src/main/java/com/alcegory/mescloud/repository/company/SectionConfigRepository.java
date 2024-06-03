package com.alcegory.mescloud.repository.company;

import com.alcegory.mescloud.model.entity.company.SectionConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionConfigRepository extends JpaRepository<SectionConfigEntity, Long> {
}

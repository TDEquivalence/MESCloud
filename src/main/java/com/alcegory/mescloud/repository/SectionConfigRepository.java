package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.SectionConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionConfigRepository extends JpaRepository<SectionConfigEntity, Long> {
}

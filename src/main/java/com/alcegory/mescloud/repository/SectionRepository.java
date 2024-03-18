package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, Long> {
}

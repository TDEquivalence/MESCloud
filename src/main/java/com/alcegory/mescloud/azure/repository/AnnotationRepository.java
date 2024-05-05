package com.alcegory.mescloud.azure.repository;

import com.alcegory.mescloud.azure.model.AnnotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnotationRepository extends JpaRepository<AnnotationEntity, Long> {
}

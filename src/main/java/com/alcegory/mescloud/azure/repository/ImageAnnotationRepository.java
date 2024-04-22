package com.alcegory.mescloud.azure.repository;

import com.alcegory.mescloud.azure.model.ImageAnnotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageAnnotationRepository extends JpaRepository<ImageAnnotationEntity, Long> {
}

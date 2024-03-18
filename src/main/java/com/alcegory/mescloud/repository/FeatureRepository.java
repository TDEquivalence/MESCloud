package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Long> {
}

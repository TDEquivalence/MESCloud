package com.tde.mescloud.repository;

import com.tde.mescloud.model.FactoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactoryRepository extends JpaRepository<FactoryEntity, Long> {
}

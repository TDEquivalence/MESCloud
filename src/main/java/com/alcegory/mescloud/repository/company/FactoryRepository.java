package com.alcegory.mescloud.repository.company;

import com.alcegory.mescloud.model.entity.company.FactoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactoryRepository extends JpaRepository<FactoryEntity, Long> {
}

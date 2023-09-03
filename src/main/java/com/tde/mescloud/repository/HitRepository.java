package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.HitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {
}

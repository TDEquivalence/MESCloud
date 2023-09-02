package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.ImsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImsRepository extends JpaRepository<ImsEntity, Long> {
}

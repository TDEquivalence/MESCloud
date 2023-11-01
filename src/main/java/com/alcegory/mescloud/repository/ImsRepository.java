package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ImsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImsRepository extends JpaRepository<ImsEntity, Long> {

    Optional<ImsEntity> findByCode(String code);
}

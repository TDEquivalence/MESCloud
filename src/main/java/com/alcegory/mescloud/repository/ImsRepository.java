package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.ImsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImsRepository extends JpaRepository<ImsEntity, Long> {

    ImsEntity findByCode(String code);
}

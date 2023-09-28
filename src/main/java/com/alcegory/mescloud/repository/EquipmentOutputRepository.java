package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentOutputRepository extends JpaRepository<EquipmentOutputEntity, Long> {

    Optional<EquipmentOutputEntity> findByCode(String equipmentOutputCode);
}

package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentOutputRepository extends JpaRepository<EquipmentOutputEntity, Long> {

    EquipmentOutputEntity findByCode(String equipmentOutputCode);
}

package com.tde.mescloud.repository;

import com.tde.mescloud.model.entity.EquipmentOutputAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentOutputAliasRepository extends JpaRepository<EquipmentOutputAliasEntity, Long> {
}

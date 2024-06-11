package com.alcegory.mescloud.repository;

import com.alcegory.mescloud.model.entity.EquipmentOutputAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentOutputAliasRepository extends JpaRepository<EquipmentOutputAliasEntity, Long> {

    EquipmentOutputAliasEntity findByAlias(String alias);

    boolean existsByAlias(String alias);
}

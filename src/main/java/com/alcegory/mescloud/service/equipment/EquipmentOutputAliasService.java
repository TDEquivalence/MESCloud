package com.alcegory.mescloud.service.equipment;

import com.alcegory.mescloud.model.entity.EquipmentOutputAliasEntity;

import java.util.List;

public interface EquipmentOutputAliasService {

    EquipmentOutputAliasEntity saveAndUpdate(EquipmentOutputAliasEntity alias);

    List<EquipmentOutputAliasEntity> saveAll(List<EquipmentOutputAliasEntity> aliasList);

    boolean existsByAlias(String alias);

    EquipmentOutputAliasEntity findByAlias(String alias);
}

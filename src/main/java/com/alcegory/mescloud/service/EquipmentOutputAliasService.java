package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.entity.EquipmentOutputAliasEntity;

import java.util.List;

public interface EquipmentOutputAliasService {

    EquipmentOutputAliasEntity saveAndUpdate(EquipmentOutputAliasEntity alias);

    List<EquipmentOutputAliasEntity> saveAll(List<EquipmentOutputAliasEntity> aliasList);

    boolean isAliasUnique(String alias);

    EquipmentOutputAliasEntity findByAlias(String alias);
}

package com.alcegory.mescloud.service.equipment;

import com.alcegory.mescloud.model.entity.EquipmentOutputAliasEntity;
import com.alcegory.mescloud.repository.equipment.EquipmentOutputAliasRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log
public class EquipmentOutputAliasServiceImpl implements EquipmentOutputAliasService {

    private final EquipmentOutputAliasRepository repository;

    @Override
    public EquipmentOutputAliasEntity saveAndUpdate(EquipmentOutputAliasEntity entity) {
        return repository.save(entity);
    }

    @Override
    public List<EquipmentOutputAliasEntity> saveAll(List<EquipmentOutputAliasEntity> aliasList) {
        return repository.saveAll(aliasList);
    }

    @Override
    public boolean existsByAlias(String alias) {
        return repository.existsByAlias(alias);
    }

    @Override
    public EquipmentOutputAliasEntity findByAlias(String alias) {
        return repository.findByAlias(alias);
    }
}

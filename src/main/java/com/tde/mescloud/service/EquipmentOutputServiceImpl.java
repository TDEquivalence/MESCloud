package com.tde.mescloud.service;

import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.converter.EquipmentOutputConverter;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.repository.EquipmentOutputRepository;
import org.springframework.stereotype.Service;

@Service
public class EquipmentOutputServiceImpl implements EquipmentOutputService {

    private final EquipmentOutputRepository repository;
    private final EquipmentOutputConverter converter;

    public EquipmentOutputServiceImpl(EquipmentOutputRepository repository,
                                      EquipmentOutputConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }


    @Override
    public EquipmentOutput findByCode(String equipmentOutputCode) {
        EquipmentOutputEntity entity = repository.findByCode(equipmentOutputCode);
        return converter.convertToDomainObject(entity);
    }
}

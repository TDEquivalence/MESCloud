package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.EquipmentOutputConverter;
import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.repository.EquipmentOutputRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EquipmentOutputServiceImpl implements EquipmentOutputService {

    private final EquipmentOutputRepository repository;
    private final EquipmentOutputConverter converter;


    @Override
    public EquipmentOutputDto findByCode(String equipmentOutputCode) {
        EquipmentOutputEntity entity = repository.findByCode(equipmentOutputCode);
        return converter.toDto(entity);
    }
}

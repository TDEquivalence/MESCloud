package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.EquipmentOutputConverter;
import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.repository.EquipmentOutputRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class EquipmentOutputServiceImpl implements EquipmentOutputService {

    private final EquipmentOutputRepository repository;
    private final EquipmentOutputConverter converter;


    @Override
    public Optional<EquipmentOutputDto> findByCode(String equipmentOutputCode) {

        Optional<EquipmentOutputEntity> entity = repository.findByCode(equipmentOutputCode);
        if (entity.isEmpty()) {
            log.warning(() -> String.format("Unable to find an equipment output with the code [%s]", equipmentOutputCode));
            return Optional.empty();
        }

        EquipmentOutputDto equipmentOutput = converter.toDto(entity.get());
        return Optional.of(equipmentOutput);
    }
}

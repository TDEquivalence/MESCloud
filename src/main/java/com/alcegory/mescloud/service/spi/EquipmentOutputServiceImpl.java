package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.repository.EquipmentOutputRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.EquipmentOutputDto;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import com.alcegory.mescloud.service.EquipmentOutputService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class EquipmentOutputServiceImpl implements EquipmentOutputService {

    private final EquipmentOutputRepository repository;
    private final GenericConverter<EquipmentOutputEntity, EquipmentOutputDto> converter;


    @Override
    public Optional<EquipmentOutputDto> findByCode(String equipmentOutputCode) {

        Optional<EquipmentOutputEntity> entity = repository.findByCode(equipmentOutputCode);
        if (entity.isEmpty()) {
            log.warning(() -> String.format("Unable to find an equipment output with the code [%s]", equipmentOutputCode));
            return Optional.empty();
        }

        EquipmentOutputDto equipmentOutput = converter.toDto(entity.get(), EquipmentOutputDto.class);
        return Optional.of(equipmentOutput);
    }

    @Override
    public EquipmentOutputDto save(EquipmentOutputEntity entity) {
        EquipmentOutputEntity persistedEntity = repository.save(entity);
        return converter.toDto(persistedEntity, EquipmentOutputDto.class);
    }

    @Override
    public List<EquipmentOutputEntity> saveAll(List<EquipmentOutputEntity> equipmentOutputToUpdate) {
        return repository.saveAll(equipmentOutputToUpdate);
    }

    @Override
    public List<Long> findIdsByCountingEquipmentId(Long equipmentId) {
        return repository.findIdsByCountingEquipmentId(equipmentId);
    }

    @Override
    public Long findIdByCountingEquipmentId(Long equipmentId) {
        return repository.findIdByCountingEquipmentId(equipmentId);
    }
}

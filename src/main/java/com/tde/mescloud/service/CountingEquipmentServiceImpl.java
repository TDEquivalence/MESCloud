package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.repository.CountingEquipmentProjection;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private CountingEquipmentRepository repository;
    private CountingEquipmentConverter converter;

    @Override
    public List<CountingEquipmentDto> findAll() {
        Iterable<CountingEquipmentProjection> countingEquipments = repository.findAllWithActiveProductionOrderCode();
        return converter.projectionToDto(countingEquipments);
    }

    @Override
    public Optional<CountingEquipmentDto> findById(long id) {

        Optional<CountingEquipmentProjection> entityOpt = repository.findProjectionById(id);
        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }

        CountingEquipmentDto counterRecordDto = converter.toDto(entityOpt.get());
        return Optional.of(counterRecordDto);
    }

    @Override
    public Optional<CountingEquipmentDto> findByCode(String code) {
        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByCode(code);
        if (countingEquipmentOpt.isEmpty()) {
            //TODO: Log
            return Optional.empty();
        }

        CountingEquipmentDto countingEquipmentDto = converter.convertToDto(countingEquipmentOpt.get());
        return Optional.of(countingEquipmentDto);
    }

    @Override
    public CountingEquipmentDto save(CountingEquipmentDto countingEquipment) {
        CountingEquipmentEntity countingEquipmentEntity = converter.convertToEntity(countingEquipment);
        CountingEquipmentEntity persistedCountingEquipment = repository.save(countingEquipmentEntity);
        return converter.convertToDto(persistedCountingEquipment);
    }
}

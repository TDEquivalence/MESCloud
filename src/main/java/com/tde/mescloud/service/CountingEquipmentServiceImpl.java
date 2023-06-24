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
        Iterable<CountingEquipmentProjection> countingEquipments = repository.findAllWithProductionOrderStatus();
        return converter.projectionToDto(countingEquipments);
    }

    @Override
    public Optional<CountingEquipmentDto> findById(long id) {

        Optional<CountingEquipmentEntity> entityOpt = repository.findById(id);
        if(entityOpt.isEmpty()) {
            return Optional.empty();
        }

        CountingEquipmentDto counterRecordDto = converter.toDto(entityOpt.get());
        return Optional.of(counterRecordDto);
    }
}

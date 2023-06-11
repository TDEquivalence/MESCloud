package com.tde.mescloud.service;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private CountingEquipmentRepository countingEquipmentRepository;
    private CountingEquipmentConverter countingEquipmentConverter;

    public List<CountingEquipment> findAll() {
        Iterable<CountingEquipmentEntity> countingEquipmentEntities = countingEquipmentRepository.findAll();
        return countingEquipmentConverter.convertToDomainObject(countingEquipmentEntities);
    }

    @Override
    public Optional<CountingEquipment> findById(long id) {
        Optional<CountingEquipmentEntity> entityOpt = countingEquipmentRepository.findById(id);
        if(entityOpt.isEmpty()) {
            return Optional.empty();
        }

        CountingEquipment countingEquipment = countingEquipmentConverter.convertToDomainObject(entityOpt.get());
        return Optional.of(countingEquipment);
    }
}

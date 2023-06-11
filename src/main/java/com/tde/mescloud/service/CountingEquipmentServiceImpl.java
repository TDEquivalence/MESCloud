package com.tde.mescloud.service;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private CountingEquipmentRepository countingEquipmentRepository;
    private CountingEquipmentConverter countingEquipmentConverter;

    public List<CountingEquipment> findAll() {
        Iterable<CountingEquipmentEntity> countingEquipmentEntities = countingEquipmentRepository.findAll();
        return countingEquipmentConverter.convertToDomainObject(countingEquipmentEntities);
    }
}

package com.tde.mescloud.service;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private CountingEquipmentRepository repository;
    private CountingEquipmentConverter converter;

    public List<CountingEquipment> findAll() {

        List<CountingEquipmentEntity> activeEquipments = repository.findByProductionOrderStatus(true);
        List<CountingEquipmentEntity> pausedEquipments = repository.findByProductionOrderStatus(false);

        List<CountingEquipment> equipments = new ArrayList<>(activeEquipments.size() + pausedEquipments.size());
        equipments.addAll(convertWithActivityStatus(activeEquipments, true));
        equipments.addAll(convertWithActivityStatus(pausedEquipments, false));

        return equipments;
    }

    private List<CountingEquipment> convertWithActivityStatus(List<CountingEquipmentEntity> entities, boolean areActive) {

        List<CountingEquipment> countingEquipments = new ArrayList<>(entities.size());
        for (CountingEquipmentEntity entity : entities) {
            CountingEquipment countingEquipment = converter.convertToDomainObject(entity);
            countingEquipment.setHasActiveProductionOrder(areActive);
            countingEquipments.add(countingEquipment);
        }

        return countingEquipments;
    }

    @Override
    public Optional<CountingEquipment> findById(long id) {

        Optional<CountingEquipmentEntity> entityOpt = repository.findById(id);
        if(entityOpt.isEmpty()) {
            return Optional.empty();
        }

        CountingEquipment countingEquipment = converter.convertToDomainObject(entityOpt.get());
        return Optional.of(countingEquipment);
    }
}

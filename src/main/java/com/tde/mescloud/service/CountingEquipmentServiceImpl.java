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

    private CountingEquipmentRepository countingEquipmentRepository;
    private CountingEquipmentConverter countingEquipmentConverter;

    public List<CountingEquipment> findAll() {

        List<CountingEquipmentEntity> activeEquipments =
                countingEquipmentRepository.findByProductionOrderStatus(true);
        List<CountingEquipmentEntity> pausedEquipments =
                countingEquipmentRepository.findByProductionOrderStatus(false);

        List<CountingEquipment> countingEquipments = new ArrayList<>(activeEquipments.size() + pausedEquipments.size());
        for (CountingEquipmentEntity entity : activeEquipments) {
            CountingEquipment countingEquipment = convertWithProductionOrderStatus(entity, true);
            countingEquipments.add(countingEquipment);
        }

        for (CountingEquipmentEntity entity : pausedEquipments) {
            CountingEquipment countingEquipment = convertWithProductionOrderStatus(entity, false);
            countingEquipments.add(countingEquipment);
        }

        return countingEquipments;
    }

    private CountingEquipment convertWithProductionOrderStatus(CountingEquipmentEntity entity, boolean isActive) {
        CountingEquipment countingEquipment = countingEquipmentConverter.convertToDomainObject(entity);
        countingEquipment.setHasActiveProductionOrder(isActive);
        return countingEquipment;
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

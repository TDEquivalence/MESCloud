package com.tde.mescloud.service;

import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.ImsEntity;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private CountingEquipmentRepository repository;
    private CountingEquipmentConverter converter;
    private ImsService imsService;

    @Override
    public List<CountingEquipmentDto> findAllWithLastProductionOrder() {
        List<CountingEquipmentEntity> persistedCountingEquipments = repository.findAllWithLastProductionOrder();
        return convertToDtoWithActiveProductionOrder(persistedCountingEquipments);
    }

    private List<CountingEquipmentDto> convertToDtoWithActiveProductionOrder(List<CountingEquipmentEntity> entities) {

        List<CountingEquipmentDto> countingEquipmentDtos = new ArrayList<>(entities.size());
        for (CountingEquipmentEntity entity : entities) {
            CountingEquipmentDto dto = convertToDtoWithActiveProductionOrder(entity);
            countingEquipmentDtos.add(dto);
        }

        return countingEquipmentDtos;
    }

    private CountingEquipmentDto convertToDtoWithActiveProductionOrder(CountingEquipmentEntity entity) {

        CountingEquipmentDto dto = converter.convertToDto(entity);

        if (hasSingleActiveProductionOrder(entity)) {
            dto.setProductionOrderCode(entity.getProductionOrders().get(0).getCode());
        }

        return dto;
    }

    private boolean hasSingleActiveProductionOrder(CountingEquipmentEntity entity) {

        return entity.getProductionOrders() != null &&
                entity.getProductionOrders().size() == 1 &&
                !entity.getProductionOrders().get(0).isCompleted();
    }

    @Override
    public Optional<CountingEquipmentDto> findById(long id) {

        CountingEquipmentEntity countingEquipment = repository.findByIdWithActiveProductionOrder(id);
        if (countingEquipment.getOutputs().isEmpty()) {
            log.warning(() -> String.format("No Counting Equipment found for id: [%s]", id));
            return Optional.empty();
        }

        CountingEquipmentDto dto = convertToDtoWithActiveProductionOrder(countingEquipment);
        return Optional.of(dto);
    }

    @Override
    public Optional<CountingEquipmentDto> findByCode(String code) {

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByCode(code);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("No Counting Equipment found for code: [%s]", code));
            return Optional.empty();
        }

        CountingEquipmentDto countingEquipmentDto = converter.convertToDto(countingEquipmentOpt.get());
        return Optional.of(countingEquipmentDto);
    }

    @Override
    public CountingEquipmentDto create(CountingEquipmentDto countingEquipment) {
        CountingEquipmentEntity countingEquipmentEntity = converter.convertToEntity(countingEquipment);
        return create(countingEquipmentEntity);
    }

    @Override
    public CountingEquipmentDto create(CountingEquipmentEntity countingEquipment) {
        CountingEquipmentEntity persistedCountingEquipment = repository.save(countingEquipment);
        return converter.convertToDto(persistedCountingEquipment);
    }

    @Override
    public Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus) {
        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByCode(equipmentCode);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("No Counting Equipment was found with the code [%s]", equipmentCode));
            return Optional.empty();
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        countingEquipment.setEquipmentStatus(equipmentStatus);
        CountingEquipmentEntity updatedCountingEquipment = repository.save(countingEquipment);

        CountingEquipmentDto updatedCountingEquipmentDto = converter.convertToDto(updatedCountingEquipment);
        return Optional.of(updatedCountingEquipmentDto);
    }

    @Override
    public Optional<CountingEquipmentDto> updateIms(Long equipmentId, Long imsId) {
        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findById(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(String.format("Unable to set IMS - no counting equipment found with id [%s]", equipmentId));
            return Optional.empty();
        }

        if (!imsService.isValidAndFree(imsId)) {
            log.warning(String.format("IMS with ID [%s] either does NOT exist or is already in use", imsId));
            return Optional.empty();
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        ImsEntity imsEntity = new ImsEntity();
        imsEntity.setId(imsId);
        countingEquipment.setIms(imsEntity);

        CountingEquipmentDto countingEquipmentDto = create(countingEquipment);
        return Optional.of(countingEquipmentDto);
    }
}

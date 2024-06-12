package com.alcegory.mescloud.service.equipment;

import com.alcegory.mescloud.model.converter.CountingEquipmentConverter;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.repository.equipment.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private static final String COUNTING_EQUIPMENT_ID_NOT_FOUND = "No Counting Equipment found for id: [%s]";
    private static final String COUNTING_EQUIPMENT_CODE_NOT_FOUND = "No Counting Equipment found for code: [%s]";

    private final CountingEquipmentRepository repository;
    private final CountingEquipmentConverter converter;

    @Override
    public List<CountingEquipmentDto> findAllWithLastProductionOrder(long sectionId) {
        List<CountingEquipmentEntity> persistedCountingEquipments = repository.findAllWithLastProductionOrder(sectionId);
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
        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByIdWithLastProductionOrder(id);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format(COUNTING_EQUIPMENT_ID_NOT_FOUND, id));
            return Optional.empty();
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        if (countingEquipment.getOutputs().isEmpty()) {
            log.warning(() -> String.format(COUNTING_EQUIPMENT_ID_NOT_FOUND, id));
            return Optional.empty();
        }

        CountingEquipmentDto dto = convertToDtoWithActiveProductionOrder(countingEquipment);
        return Optional.of(dto);
    }


    @Override
    public Optional<CountingEquipmentDto> findByCode(String code) {

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByCode(code);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format(COUNTING_EQUIPMENT_CODE_NOT_FOUND, code));
            return Optional.empty();
        }

        CountingEquipmentDto countingEquipmentDto = converter.convertToDto(countingEquipmentOpt.get());
        return Optional.of(countingEquipmentDto);
    }

    @Override
    public Optional<CountingEquipmentEntity> findEntityByCode(String code) {

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByCode(code);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format(COUNTING_EQUIPMENT_CODE_NOT_FOUND, code));
            return Optional.empty();
        }

        return countingEquipmentOpt;
    }

    @Override
    public CountingEquipmentDto save(CountingEquipmentDto countingEquipment) {
        CountingEquipmentEntity countingEquipmentEntity = converter.convertToEntity(countingEquipment);
        CountingEquipmentEntity persistedCountingEquipment = repository.save(countingEquipmentEntity);
        return converter.convertToDto(persistedCountingEquipment);
    }

    @Override
    public CountingEquipmentDto saveAndGetDto(CountingEquipmentEntity countingEquipment) {
        CountingEquipmentEntity persistedCountingEquipment = save(countingEquipment);
        return converter.convertToDto(persistedCountingEquipment);
    }

    @Override
    public CountingEquipmentEntity save(CountingEquipmentEntity countingEquipment) {
        return repository.save(countingEquipment);
    }

    @Override
    public boolean hasActiveProductionOrder(CountingEquipmentEntity countingEquipment) {
        return !countingEquipment.getProductionOrders().isEmpty() &&
                !countingEquipment.getProductionOrders().get(0).isCompleted();
    }

    @Override
    public CountingEquipmentDto setOperationStatus(CountingEquipmentEntity countingEquipment, CountingEquipmentEntity.OperationStatus status) {
        countingEquipment.setOperationStatus(status);
        repository.save(countingEquipment);
        return converter.convertToDto(countingEquipment);
    }

    @Override
    public void setOperationStatusByCode(String equipmentCode, CountingEquipmentEntity.OperationStatus status) {
        Optional<CountingEquipmentEntity> countingEquipmentEntityOptional = repository.findByCode(equipmentCode);
        countingEquipmentEntityOptional.ifPresent(countingEquipmentEntity -> setOperationStatus(countingEquipmentEntity, status));

        if (countingEquipmentEntityOptional.isEmpty()) {
            log.info(() -> String.format("Equipment with code [%s] not found", equipmentCode));
        }
    }

    @Override
    public List<Long> findAllIds() {
        return repository.findAllIds();
    }

    @Override
    public Long findIdByAlias(String alias) {
        return repository.findIdByAlias(alias);
    }

    @Override
    public Optional<CountingEquipmentEntity> findEntityById(Long equipmentId) {
        if (equipmentId == null) {
            return Optional.empty();
        }
        return repository.findById(equipmentId);
    }

    @Override
    public Optional<CountingEquipmentEntity> findByCodeWithLastStatusRecord(String equipmentCode) {
        return repository.findByCodeWithLastStatusRecord(equipmentCode);
    }

    @Override
    public Optional<CountingEquipmentEntity> findByIdWithLastProductionOrder(Long id) {
        return repository.findByIdWithLastProductionOrder(id);
    }

    @Override
    @Transactional
    public Optional<CountingEquipmentEntity> findEquipmentTemplate(long id) {
        return repository.findEquipmentWithTemplateById(id);
    }

    @Override
    public Double getAverageQualityTargetDividedByTotalCount() {
        return repository.findSumQualityTargetDividedByTotalCount();
    }

    @Override
    public Double getAverageAvailabilityTargetDividedByTotalCount() {
        return repository.findSumAvailabilityTargetDividedByTotalCount();
    }

    @Override
    public Double getAveragePerformanceTargetDividedByTotalCount() {
        return repository.findSumPerformanceTargetDividedByTotalCount();
    }

    @Override
    public Double getAverageOverallEquipmentEffectivenessTargetDividedByTotalCount() {
        return repository.findSumOverallEquipmentEffectivenessTargetDividedByTotalCount();
    }

    public Double getAverageTheoreticalProduction() {
        return repository.findSumTheoreticalProductionDividedByTotalCount();
    }
}
package com.tde.mescloud.service;

import com.tde.mescloud.exception.ActiveProductionOrderException;
import com.tde.mescloud.exception.IncompleteConfigurationException;
import com.tde.mescloud.model.converter.CountingEquipmentConverter;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.RequestConfigurationDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ImsEntity;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private static final int MIN_P_TIMER = 10;

    private CountingEquipmentRepository repository;
    private CountingEquipmentConverter converter;
    private ImsService imsService;
    private EquipmentStatusRecordService statusRecordService;
    private ProductionOrderService productionOrderService;

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
        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByIdWithActiveProductionOrder(id);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("No Counting Equipment found for id: [%s]", id));
            return Optional.empty();
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
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
    public CountingEquipmentDto save(CountingEquipmentDto countingEquipment) {
        CountingEquipmentEntity countingEquipmentEntity = converter.convertToEntity(countingEquipment);
        return save(countingEquipmentEntity);
    }

    @Override
    public CountingEquipmentDto save(CountingEquipmentEntity countingEquipment) {
        CountingEquipmentEntity persistedCountingEquipment = repository.save(countingEquipment);
        return converter.convertToDto(persistedCountingEquipment);
    }

    @Override
    public Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus) {

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByCodeWithLastStatusRecord(equipmentCode);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("No Counting Equipment was found with the code [%s]", equipmentCode));
            return Optional.empty();
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        countingEquipment.setEquipmentStatus(equipmentStatus);
        CountingEquipmentEntity updatedCountingEquipment = repository.save(countingEquipment);
        //TODO: Refactor

        if (hasStatusChanged(countingEquipment, equipmentStatus)) {
            statusRecordService.save(countingEquipment.getId(), equipmentStatus);
        }

        CountingEquipmentDto updatedCountingEquipmentDto = converter.convertToDto(updatedCountingEquipment);
        return Optional.of(updatedCountingEquipmentDto);
    }

    private boolean hasStatusChanged(CountingEquipmentEntity countingEquipment, int equipmentStatus) {
        return countingEquipment.getEquipmentStatusRecords().isEmpty() ||
                countingEquipment.getEquipmentStatusRecords().get(0).getEquipmentStatus().getStatus() != equipmentStatus;
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
        setIms(countingEquipment, imsId);

        CountingEquipmentDto countingEquipmentDto = save(countingEquipment);
        return Optional.of(countingEquipmentDto);
    }

    @Override
    public CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request)
            throws IncompleteConfigurationException, EmptyResultDataAccessException, ActiveProductionOrderException {

        if (containsNullProperty(request)) {
            throw new IncompleteConfigurationException("Counting equipment configuration is incomplete. All properties (alias, outputs, and imsCode) must be specified.");
        }

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByIdWithActiveProductionOrder(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            String msg = String.format("Counting equipment with id [%s] does not exist.", equipmentId);
            log.warning(msg);
            throw new EmptyResultDataAccessException(msg, 1);
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        if (hasActiveProductionOrder(countingEquipment)) {
            String msg = String.format("Updating equipment configuration failed: equipment with id [%s] has an active production order", equipmentId);
            log.info(msg);
            throw new ActiveProductionOrderException(msg);
        }

        updateEquipmentConfiguration(countingEquipment, request);
        repository.save(countingEquipment);
        return converter.convertToDto(countingEquipment);
    }

    private boolean containsNullProperty(RequestConfigurationDto countingEquipmentDto) {
        return countingEquipmentDto.getAlias() == null ||
                countingEquipmentDto.getOutputs() == null ||
                countingEquipmentDto.getImsDto() == null;
    }

    private boolean hasActiveProductionOrder(CountingEquipmentEntity countingEquipment) {
        return !countingEquipment.getProductionOrders().isEmpty() &&
                !countingEquipment.getProductionOrders().get(0).isCompleted();
    }

    private void updateEquipmentConfiguration(CountingEquipmentEntity persistedEquipment, RequestConfigurationDto request) {
        CountingEquipmentEntity countingEquipmentConfig = converter.convertToEntity(request);
        ensureMinimumPTimer(countingEquipmentConfig);

        updateFrom(persistedEquipment, countingEquipmentConfig);
    }

    private void updateFrom(CountingEquipmentEntity toUpdate, CountingEquipmentEntity updateFrom) {
        toUpdate.setAlias(updateFrom.getAlias());
        toUpdate.setPTimerCommunicationCycle(updateFrom.getPTimerCommunicationCycle());
        toUpdate.setTheoreticalProduction(updateFrom.getTheoreticalProduction());
        toUpdate.setQualityTarget(updateFrom.getQualityTarget());
        toUpdate.setPerformanceTarget(updateFrom.getPerformanceTarget());
        toUpdate.setAvailabilityTarget(updateFrom.getAvailabilityTarget());
        toUpdate.setOverallEquipmentEffectivenessTarget(updateFrom.getOverallEquipmentEffectivenessTarget());
        updateOutputs(toUpdate, updateFrom);
        updateIms(toUpdate, updateFrom);
    }

    private void updateOutputs(CountingEquipmentEntity toUpdate, CountingEquipmentEntity updateFrom) {
        List<EquipmentOutputEntity> equipmentOutputToUpdate = toUpdate.getOutputs();
        List<EquipmentOutputEntity> equipmentOutputUpdateFrom = updateFrom.getOutputs();

        Map<String, EquipmentOutputEntity> equipmentOutputUpdateFromMap = equipmentOutputUpdateFrom.stream()
                .collect(Collectors.toMap(EquipmentOutputEntity::getCode, Function.identity()));

        equipmentOutputToUpdate.forEach(outputToUpdate -> {
            EquipmentOutputEntity outputUpdateFrom = equipmentOutputUpdateFromMap.get(outputToUpdate.getCode());
            if (outputUpdateFrom != null) {
                outputToUpdate.setAlias(outputUpdateFrom.getAlias());
            }
        });
    }

    private void updateIms(CountingEquipmentEntity toUpdate, CountingEquipmentEntity updateFrom) {
        String imsCodeToUpdateFrom = updateFrom.getIms().getCode();
        ImsEntity imsDb = imsService.findByCode(imsCodeToUpdateFrom);

        if (imsDb != null) {
            toUpdate.setIms(imsDb);
        }

        if (updateFrom.getIms() != null && imsDb == null) {
            toUpdate.getIms().setCode(imsCodeToUpdateFrom);
        }

        if (toUpdate.getIms() == null) {
            toUpdate.setIms(updateFrom.getIms());
        }
    }


    private void ensureMinimumPTimer(CountingEquipmentEntity countingEquipmentEntity) {
        int currentPTimer = countingEquipmentEntity.getPTimerCommunicationCycle();
        countingEquipmentEntity.setPTimerCommunicationCycle(Math.max(MIN_P_TIMER, currentPTimer));
    }

    private void setIms(CountingEquipmentEntity countingEquipment, Long imsId) {
        ImsEntity imsEntity = new ImsEntity();
        imsEntity.setId(imsId);
        countingEquipment.setIms(imsEntity);
    }
}

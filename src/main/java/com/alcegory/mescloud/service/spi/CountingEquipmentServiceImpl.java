package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.EquipmentNotFoundException;
import com.alcegory.mescloud.exception.ImsNotFoundException;
import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.converter.CountingEquipmentConverter;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.EquipmentOutputAliasEntity;
import com.alcegory.mescloud.model.entity.EquipmentOutputEntity;
import com.alcegory.mescloud.model.entity.ImsEntity;
import com.alcegory.mescloud.repository.CountingEquipmentRepository;
import com.alcegory.mescloud.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Log
public class CountingEquipmentServiceImpl implements CountingEquipmentService {

    private static final int MIN_P_TIMER = 10;

    private final CountingEquipmentRepository repository;
    private final EquipmentOutputService outputService;
    private final EquipmentOutputAliasService aliasService;
    private final ProductionOrderService productionOrderService;
    private final CountingEquipmentConverter converter;
    private final ImsService imsService;
    private final GenericConverter<ImsEntity, ImsDto> imsConverter;
    private final EquipmentStatusRecordService statusRecordService;

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
        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByIdWithLastProductionOrder(id);
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
    public CountingEquipmentDto updateIms(Long equipmentId, Long imsId)
            throws EquipmentNotFoundException, ImsNotFoundException, IllegalStateException {

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByIdWithLastProductionOrder(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            String msg = String.format("Unable to set IMS - no counting equipment found with id [%s]", equipmentId);
            log.warning(msg);
            throw new EquipmentNotFoundException(msg);
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        if (hasActiveProductionOrder(countingEquipment)) {
            String msg = String.format("Unable to set IMS - counting equipment [%s] already has an active production order", countingEquipment.getAlias());
            log.warning(msg);
            throw new ActiveProductionOrderException("Counting equipment");
        }

        Optional<ImsDto> imsOpt = imsService.findById(imsId);
        if (imsOpt.isEmpty()) {
            String msg = String.format("Unable to find an IMS with id [%s]", imsId);
            log.warning(msg);
            throw new ImsNotFoundException(msg);
        }

        ImsDto ims = imsOpt.get();
        if (ims.getCountingEquipmentId() != null) {
            String msg = String.format("IMS with id [%s] is already in use by equipment [%s]", imsId, ims.getCountingEquipmentId());
            log.warning(msg);
            throw new IllegalStateException(msg);
        }

        ImsEntity imsEntity = new ImsEntity();
        imsEntity.setId(ims.getId());
        countingEquipment.setIms(imsEntity);

        return save(countingEquipment);
    }

    @Override
    public CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request)
            throws IncompleteConfigurationException, EmptyResultDataAccessException, ActiveProductionOrderException {

        if (containsNullProperty(request)) {
            throw new IncompleteConfigurationException("Counting equipment configuration is incomplete: properties alias and outputs must be specified.");
        }

        Optional<CountingEquipmentEntity> countingEquipmentOpt = repository.findByIdWithLastProductionOrder(equipmentId);
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
                countingEquipmentDto.getOutputs() == null;
    }

    private boolean hasActiveProductionOrder(CountingEquipmentEntity countingEquipment) {
        return !countingEquipment.getProductionOrders().isEmpty() &&
                !countingEquipment.getProductionOrders().get(0).isCompleted();
    }


    @Override
    public boolean hasEquipmentAssociatedProductionOrder(String equipmentCode) {
        Optional<CountingEquipmentDto> countingEquipmentDto = findByCode(equipmentCode);
        if (countingEquipmentDto.isEmpty()) {
            return false;
        }
        String productionOrderCode = countingEquipmentDto.get().getProductionOrderCode();
        if (productionOrderCode == null) {
            log.info(() -> String.format("Equipment with code [%s], doesn't  have associated production order ", equipmentCode));
        }
        return productionOrderService.isCompleted(productionOrderCode);
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
        updateOutputsAlias(toUpdate, updateFrom);
        updateIms(toUpdate, updateFrom.getIms());
    }

    private void updateOutputsAlias(CountingEquipmentEntity toUpdate, CountingEquipmentEntity updateFrom) {
        List<EquipmentOutputEntity> equipmentOutputToUpdate = toUpdate.getOutputs();
        List<EquipmentOutputEntity> equipmentOutputUpdateFrom = updateFrom.getOutputs();
        Map<String, EquipmentOutputEntity> outputMap = new HashMap<>();

        for (EquipmentOutputEntity outputUpdateFrom : equipmentOutputUpdateFrom) {
            outputMap.put(outputUpdateFrom.getCode(), outputUpdateFrom);
        }

        for (EquipmentOutputEntity outputToUpdate : equipmentOutputToUpdate) {
            EquipmentOutputEntity outputUpdateFrom = outputMap.get(outputToUpdate.getCode());

            if (outputUpdateFrom != null) {
                String alias = outputUpdateFrom.getAlias().getAlias();
                if (!aliasService.isAliasUnique(alias)) {
                    EquipmentOutputAliasEntity persistedAlias = aliasService.findByAlias(alias);
                    outputToUpdate.setAlias(persistedAlias);
                } else {
                    outputToUpdate.setAlias(outputUpdateFrom.getAlias());
                }
            }
        }
        outputService.saveAll(equipmentOutputToUpdate);
    }

    private void updateIms(CountingEquipmentEntity toUpdate, ImsEntity requestIms) {

        if (requestIms == null || requestIms.getCode() == null || requestIms.getCode().isEmpty()) {
            toUpdate.setIms(null);
            return;
        }

        Optional<ImsEntity> persistedImsOpt = imsService.findByCode(requestIms.getCode());

        ImsEntity imsToUpdate = persistedImsOpt.orElseGet(() -> {
            ImsDto newIms = new ImsDto();
            newIms.setCode(requestIms.getCode());
            ImsDto persistedIms = imsService.create(newIms);
            return imsConverter.toEntity(persistedIms, ImsEntity.class);
        });

        toUpdate.setIms(imsToUpdate);
    }

    private void ensureMinimumPTimer(CountingEquipmentEntity countingEquipmentEntity) {
        int currentPTimer = countingEquipmentEntity.getPTimerCommunicationCycle();
        countingEquipmentEntity.setPTimerCommunicationCycle(Math.max(MIN_P_TIMER, currentPTimer));
    }
}
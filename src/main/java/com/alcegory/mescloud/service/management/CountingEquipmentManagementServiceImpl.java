package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.exception.*;
import com.alcegory.mescloud.model.converter.CountingEquipmentConverter;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.PlcMqttConverter;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentConfigMqttDto;
import com.alcegory.mescloud.model.dto.equipment.TemplateDto;
import com.alcegory.mescloud.model.entity.ImsEntity;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.equipment.EquipmentOutputAliasEntity;
import com.alcegory.mescloud.model.entity.equipment.EquipmentOutputEntity;
import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
import com.alcegory.mescloud.protocol.MesMqttSettings;
import com.alcegory.mescloud.security.model.SectionAuthority;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.equipment.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.alcegory.mescloud.security.model.SectionRole.ADMIN;
import static com.alcegory.mescloud.security.utility.AuthorityUtil.checkUserAndSectionRole;

@Service
@Transactional
@Log
@AllArgsConstructor
public class CountingEquipmentManagementServiceImpl implements CountingEquipmentManagementService {

    private static final int MIN_P_TIMER_IN_MINUTES = 1;
    private static final int MIN_P_TIMER_IN_SECONDS = MIN_P_TIMER_IN_MINUTES * 60;
    private static final String COUNTING_EQUIPMENT_CODE_NOT_FOUND = "No Counting Equipment found for code: [%s]";
    private final EquipmentOutputAliasService aliasService;

    private final CountingEquipmentService countingEquipmentService;
    private final EquipmentStatusRecordService statusRecordService;
    private final EquipmentOutputService outputService;
    private final UserRoleService userRoleService;
    private final ImsService imsService;

    private final MqttClient mqttClient;
    private final MesMqttSettings mqttSettings;

    private final CountingEquipmentConverter converter;
    private final PlcMqttConverter plcConverter;
    private final GenericConverter<ImsEntity, ImsDto> imsConverter;
    private final GenericConverter<TemplateEntity, TemplateDto> templateConverter;

    @Override
    public Optional<CountingEquipmentDto> updateEquipmentStatus(String equipmentCode, int equipmentStatus) {
        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentService.findByCodeWithLastStatusRecord(equipmentCode);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format(COUNTING_EQUIPMENT_CODE_NOT_FOUND, equipmentCode));
            return Optional.empty();
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        countingEquipment.setEquipmentStatus(equipmentStatus);
        CountingEquipmentEntity updatedCountingEquipment = countingEquipmentService.save(countingEquipment);

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
    public CountingEquipmentDto updateIms(Long equipmentId, Long imsId, Authentication authentication) {
        checkUserAndSectionRole(authentication, this.userRoleService, ADMIN);

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentService.findByIdWithLastProductionOrder(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            String msg = String.format("Unable to set IMS - no counting equipment found with id [%s]", equipmentId);
            log.warning(msg);
            throw new EquipmentNotFoundException(msg);
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        if (countingEquipmentService.hasActiveProductionOrder(countingEquipment)) {
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

        return countingEquipmentService.saveAndGetDto(countingEquipment);
    }

    @Override
    public CountingEquipmentDto updateConfiguration(long equipmentId, RequestConfigurationDto request, Authentication authentication)
            throws IncompleteConfigurationException, EmptyResultDataAccessException, ActiveProductionOrderException, MesMqttException {
        //TODO: sectionID
        userRoleService.checkSectionAuthority(authentication, 1L, SectionAuthority.ADMIN_UPDATE);

        if (containsNullProperty(request)) {
            throw new IncompleteConfigurationException("Counting equipment configuration is incomplete: properties alias and outputs must be specified.");
        }

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentService.findByIdWithLastProductionOrder(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            String msg = String.format("Counting equipment with id [%s] does not exist.", equipmentId);
            log.warning(msg);
            throw new EmptyResultDataAccessException(msg, 1);
        }

        CountingEquipmentEntity countingEquipment = countingEquipmentOpt.get();
        if (countingEquipmentService.hasActiveProductionOrder(countingEquipment)) {
            String msg = String.format("Updating equipment configuration failed: equipment with id [%s] has an active production order", equipmentId);
            log.info(msg);
            throw new ActiveProductionOrderException(msg);
        }

        updateEquipmentConfiguration(countingEquipment, request);
        publishToPlc(countingEquipment);
        countingEquipmentService.save(countingEquipment);

        return converter.convertToDto(countingEquipment);
    }

    @Override
    public void setOperationStatusByCode(String equipmentCode, CountingEquipmentEntity.OperationStatus status) {
        Optional<CountingEquipmentEntity> countingEquipmentEntityOptional = countingEquipmentService.findEntityByCode(equipmentCode);
        countingEquipmentEntityOptional.ifPresent(countingEquipmentEntity -> countingEquipmentService.setOperationStatus(countingEquipmentEntity, status));

        if (countingEquipmentEntityOptional.isEmpty()) {
            log.info(() -> String.format("Equipment with code [%s] not found", equipmentCode));
        }
    }

    private boolean containsNullProperty(RequestConfigurationDto countingEquipmentDto) {
        return countingEquipmentDto.getAlias() == null ||
                countingEquipmentDto.getOutputs() == null;
    }

    private void publishToPlc(CountingEquipmentEntity countingEquipment) throws MesMqttException {
        EquipmentConfigMqttDto equipmentConfig = plcConverter.toMqttDto(countingEquipment);
        int pTimerCommunicationCycleInSeconds = equipmentConfig.getPTimerCommunicationCycle() * 60;
        int finalPTimerCommunicationCycleInSeconds = Math.max(MIN_P_TIMER_IN_SECONDS, pTimerCommunicationCycleInSeconds);
        equipmentConfig.setPTimerCommunicationCycle(finalPTimerCommunicationCycleInSeconds);

        mqttClient.publish(mqttSettings.getProtCountPlcTopic(), equipmentConfig);
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
        toUpdate.setUnrecognizedAlarmDuration(updateFrom.getUnrecognizedAlarmDuration());
        updateOutputsAlias(toUpdate, updateFrom);
        updateIms(toUpdate, updateFrom.getIms());
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

                boolean isValidForProduction = outputUpdateFrom.isValidForProduction();
                outputToUpdate.setValidForProduction(isValidForProduction);

                String alias = outputUpdateFrom.getAlias().getAlias();

                EquipmentOutputAliasEntity persistedAlias = aliasService.findByAlias(alias);
                if (persistedAlias != null) {
                    outputToUpdate.setAlias(persistedAlias);
                } else {
                    outputToUpdate.setAlias(outputUpdateFrom.getAlias());
                }
            }
        }
        outputService.saveAll(equipmentOutputToUpdate);
    }

    private void ensureMinimumPTimer(CountingEquipmentEntity countingEquipmentEntity) {
        int currentPTimer = countingEquipmentEntity.getPTimerCommunicationCycle();
        countingEquipmentEntity.setPTimerCommunicationCycle(Math.max(MIN_P_TIMER_IN_MINUTES, currentPTimer));
    }

    @Override
    public TemplateDto findEquipmentTemplate(long id) {
        CountingEquipmentEntity countingEquipment = countingEquipmentService.findEquipmentTemplate(id)
                .orElseThrow(() -> new CountingEquipmentException("Error getting counting equipment with id " + id));

        TemplateEntity template = countingEquipment.getTemplate();

        return templateConverter.toDto(template, TemplateDto.class);
    }
}

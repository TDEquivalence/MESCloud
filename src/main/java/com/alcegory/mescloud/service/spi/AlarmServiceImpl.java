package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.exception.AlarmConfigurationNotFoundException;
import com.alcegory.mescloud.exception.AlarmNotFoundException;
import com.alcegory.mescloud.exception.EquipmentNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.*;
import com.alcegory.mescloud.model.filter.AlarmFilter;
import com.alcegory.mescloud.repository.AlarmRepository;
import com.alcegory.mescloud.service.AlarmConfigurationService;
import com.alcegory.mescloud.service.AlarmService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.service.ProductionOrderService;
import com.alcegory.mescloud.utility.BinaryUtil;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class AlarmServiceImpl implements AlarmService {

    private static final int PLC_BITS_PER_WORD = 16;

    private GenericConverter<AlarmEntity, AlarmDto> converter;
    private AlarmRepository repository;
    private AlarmConfigurationService alarmCodeService;
    private ProductionOrderService productionOrderService;
    private GenericConverter<ProductionOrderEntity, ProductionOrderDto> productionOrderConverter;
    private CountingEquipmentService countingEquipmentService;
    private GenericConverter<CountingEquipmentEntity, CountingEquipmentDto> countingEquipmentConverter;


    @Override
    public List<AlarmDto> findAllByFilter(AlarmFilter filter) {
        List<AlarmEntity> alarms = repository.findAllByFilter(filter);
        return converter.toDto(alarms, AlarmDto.class);
    }

    @Override
    public AlarmDto recognizeAlarm(Long alarmId, RequestAlarmRecognitionDto alarmRecognition, Authentication authentication)
            throws AlarmNotFoundException, IllegalAlarmStatusException {

        Optional<AlarmEntity> alarmOpt = repository.findById(alarmId);
        if (alarmOpt.isEmpty()) {
            String msg = String.format("Unable to recognize alarm: no alarm found with id [%s]", alarmId);
            log.warning(msg);
            throw new AlarmNotFoundException(msg);
        }

        AlarmEntity alarmToUpdate = alarmOpt.get();
        if (!AlarmStatus.INACTIVE.equals(alarmToUpdate.getStatus())) {
            String alarmStatusName = alarmToUpdate.getStatus() != null ? alarmToUpdate.getStatus().name() : null;
            String msg = String.format("Unable to recognize alarm record: current alarm record status is [%s]", alarmStatusName);
            log.warning(msg);
            throw new IllegalAlarmStatusException(msg);
        }

        UserEntity user = (UserEntity) authentication.getPrincipal();
        return doRecognizeAlarm(alarmToUpdate, user, alarmRecognition.getComment());
    }

    private AlarmDto doRecognizeAlarm(AlarmEntity alarmToUpdate, UserEntity user, @Nullable String comment) {

        alarmToUpdate.setCompletedBy(user);
        alarmToUpdate.setCompletedAt(new Date());
        alarmToUpdate.setComment(comment);
        alarmToUpdate.setStatus(AlarmStatus.RECOGNIZED);

        AlarmEntity updatedAlarmRecord = repository.save(alarmToUpdate);
        return converter.toDto(updatedAlarmRecord, AlarmDto.class);
    }

    @Override
    public AlarmCounts getAlarmCounts(AlarmFilter filter) {
        return repository.getAlarmCounts(filter);
    }

    @Override
    public void processPlcAlarms(PlcMqttDto plcMqttDto) throws AlarmConfigurationNotFoundException, EquipmentNotFoundException {
        boolean[][] alarmWords = BinaryUtil.toBinaryUnsigned(plcMqttDto.getAlarms(), PLC_BITS_PER_WORD);
        for (int wordIndex = 0; wordIndex < alarmWords.length; wordIndex++) {
            processWordBits(alarmWords, wordIndex, plcMqttDto);
        }
    }

    private void processWordBits(boolean[][] alarmWords, int wordIndex, PlcMqttDto plcMqttDto) throws AlarmConfigurationNotFoundException, EquipmentNotFoundException {
        for (int bitIndex = 0; bitIndex < PLC_BITS_PER_WORD; bitIndex++) {
            if (alarmWords[wordIndex][bitIndex]) {
                saveAlarm(plcMqttDto, wordIndex, bitIndex);
            }
        }
    }

    private void saveAlarm(PlcMqttDto plcMqttDto, int wordIndex, int bitIndex) throws AlarmConfigurationNotFoundException, EquipmentNotFoundException {

        Optional<AlarmConfigurationEntity> alarmConfigurationOpt = alarmCodeService.findByWordIndexAndBitIndex(wordIndex, bitIndex);
        if (alarmConfigurationOpt.isEmpty()) {
            String message = String.format("Unable to find an Alarm Configuration for word index [%s] and bit index [%s]", wordIndex, bitIndex);
            log.warning(message);
            throw new AlarmConfigurationNotFoundException(message);
        }

        Optional<CountingEquipmentDto> countingEquipmentOpt = countingEquipmentService.findByCode(plcMqttDto.getEquipmentCode());
        if (countingEquipmentOpt.isEmpty()) {
            String message = String.format("Unable to find Counting Equipment with code [%s]", plcMqttDto.getEquipmentCode());
            log.warning(message);
            throw new EquipmentNotFoundException(message);
        }

        AlarmConfigurationEntity alarmConfiguration = alarmConfigurationOpt.get();
        CountingEquipmentEntity countingEquipment = countingEquipmentConverter.toEntity(countingEquipmentOpt.get(), CountingEquipmentEntity.class);

        if (repository.isAlreadyReported(alarmConfiguration.getId(), countingEquipment.getId())) {
            log.info(() -> String.format("Alarm with code [%s] for equipment [%s] is still active.", alarmConfiguration.getCode(), countingEquipment.getAlias()));
            return;
        }

        doSaveAlarm(alarmConfiguration, countingEquipment, plcMqttDto.getProductionOrderCode());
    }

    private void doSaveAlarm(AlarmConfigurationEntity alarmConfiguration, CountingEquipmentEntity countingEquipment, String productionOrderCode) {
        AlarmEntity alarm = createAlarmEntity(alarmConfiguration, countingEquipment);
        setProductionOrder(alarm, productionOrderCode);

        repository.save(alarm);
    }

    private AlarmEntity createAlarmEntity(AlarmConfigurationEntity alarmConfiguration, CountingEquipmentEntity countingEquipment) {

        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setAlarmConfiguration(alarmConfiguration);
        alarmEntity.setEquipment(countingEquipment);
        alarmEntity.setCreatedAt(new Date());
        alarmEntity.setStatus(AlarmStatus.ACTIVE);
        return alarmEntity;
    }

    private void setProductionOrder(AlarmEntity alarm, String productionOrderCode) {

        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.findByCode(productionOrderCode);
        if (productionOrderOpt.isPresent()) {
            ProductionOrderEntity productionOrder = productionOrderConverter.toEntity(productionOrderOpt.get(), ProductionOrderEntity.class);
            alarm.setProductionOrder(productionOrder);
        } else {
            log.warning(() -> String.format("Unable to find Production Order with code [%s]", productionOrderCode));
        }
    }
}

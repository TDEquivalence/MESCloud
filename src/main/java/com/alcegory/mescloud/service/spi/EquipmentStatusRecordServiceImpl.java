package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.constant.EquipmentStatus;
import com.alcegory.mescloud.repository.EquipmentStatusRecordRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.EquipmentStatusRecordDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.EquipmentStatusRecordEntity;
import com.alcegory.mescloud.service.EquipmentStatusRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Log
public class EquipmentStatusRecordServiceImpl implements EquipmentStatusRecordService {

    private EquipmentStatusRecordRepository repository;
    private GenericConverter<EquipmentStatusRecordEntity, EquipmentStatusRecordDto> converter;

    @Override
    public EquipmentStatusRecordDto save(long equipmentId, int equipmentStatus) {
        EquipmentStatusRecordEntity equipmentStatusRecord = new EquipmentStatusRecordEntity();
        CountingEquipmentEntity countingEquipment = new CountingEquipmentEntity();
        countingEquipment.setId(equipmentId);
        equipmentStatusRecord.setCountingEquipment(countingEquipment);
        equipmentStatusRecord.setEquipmentStatus(EquipmentStatus.getByStatus(equipmentStatus));
        equipmentStatusRecord.setRegisteredAt(Timestamp.from(Instant.now()));

        EquipmentStatusRecordEntity persistedEquipmentStatus = repository.save(equipmentStatusRecord);
        return converter.toDto(persistedEquipmentStatus, EquipmentStatusRecordDto.class);
    }

    private List<EquipmentStatusRecordEntity> findRecordsForPeriodAndLastBefore(Long equipmentId,
                                                                                Timestamp startDate,
                                                                                Timestamp endDate) {

        List<EquipmentStatusRecordEntity> equipmentStatusRecords =
                repository.findRecordsForPeriodAndLastBefore(equipmentId, startDate, endDate);

        if (equipmentStatusRecords.isEmpty()) {
            log.severe(() -> String.format("No equipment status records found for equipment with id [%s]", equipmentId));
            return Collections.emptyList();
        }

        return equipmentStatusRecords;
    }

    @Override
    public Long calculateStoppageTimeInSeconds(Long equipmentId, Timestamp startDate, Timestamp endDate) {

        List<EquipmentStatusRecordEntity> records = findRecordsForPeriodAndLastBefore(equipmentId, startDate, endDate);

        if (records.size() == 1) {
            return isRecordActive(records.get(0)) ? 0L : calculateDurationInSeconds(startDate, endDate);
        }

        long lastActiveTime = startDate.getTime();
        Duration stoppageDuration = Duration.ZERO;

        for (EquipmentStatusRecordEntity equipmentStatusRecord : records) {
            if (isRecordActive(equipmentStatusRecord) && (equipmentStatusRecord.getRegisteredAt().getTime() > lastActiveTime)) {
                long stoppageInMillis = equipmentStatusRecord.getRegisteredAt().getTime() - lastActiveTime;
                stoppageDuration = stoppageDuration.plusMillis(stoppageInMillis);
            } else {
                lastActiveTime = determineLastActiveTime(equipmentStatusRecord, startDate);
            }
        }

        return stoppageDuration.getSeconds();
    }

    private boolean isRecordActive(EquipmentStatusRecordEntity equipmentStatusRecord) {
        return EquipmentStatus.ACTIVE.equals(equipmentStatusRecord.getEquipmentStatus());
    }

    private long calculateDurationInSeconds(Timestamp startDate, Timestamp endDate) {
        return Duration.between(startDate.toInstant(), endDate.toInstant()).getSeconds();
    }

    private long determineLastActiveTime(EquipmentStatusRecordEntity equipmentStatusRecord, Timestamp startDate) {
        return equipmentStatusRecord.getRegisteredAt().after(startDate) ?
                equipmentStatusRecord.getRegisteredAt().getTime() :
                startDate.getTime();
    }
}

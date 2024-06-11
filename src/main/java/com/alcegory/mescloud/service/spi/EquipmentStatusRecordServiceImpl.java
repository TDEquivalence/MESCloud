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

    private final EquipmentStatusRecordRepository repository;
    private final GenericConverter<EquipmentStatusRecordEntity, EquipmentStatusRecordDto> converter;

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

        validateInput(startDate, endDate);

        List<EquipmentStatusRecordEntity> equipmentStatusRecords = findRecordsForPeriodAndLastBefore(equipmentId, startDate, endDate);
        if (equipmentStatusRecords.isEmpty()) {
            log.info(() -> String.format("No equipment status found for equipment with id [%s]", equipmentId));
            return 0L;
        }

        long lastActiveTime = startDate.getTime();
        Duration stoppageDuratinInMillis = Duration.ZERO;
        EquipmentStatus lastEquipmentStatus = EquipmentStatus.ACTIVE;

        for (EquipmentStatusRecordEntity equipmentStatusRecord : equipmentStatusRecords) {
            lastEquipmentStatus = equipmentStatusRecord.getEquipmentStatus();
            lastActiveTime = Math.max(equipmentStatusRecord.getRegisteredAt().getTime(), lastActiveTime);

            if (EquipmentStatus.ACTIVE.equals(equipmentStatusRecord.getEquipmentStatus())) {
                long stoppageInMillis = equipmentStatusRecord.getRegisteredAt().getTime() - lastActiveTime;
                stoppageDuratinInMillis = stoppageDuratinInMillis.plusMillis(stoppageInMillis);
            }
        }

        if (!EquipmentStatus.ACTIVE.equals(lastEquipmentStatus)) {
            long stoppageInMillis = endDate.getTime() - lastActiveTime;
            stoppageDuratinInMillis = stoppageDuratinInMillis.plusMillis(stoppageInMillis);
        }

        return stoppageDuratinInMillis.getSeconds();
    }

    private void validateInput(Timestamp startDate, Timestamp endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}

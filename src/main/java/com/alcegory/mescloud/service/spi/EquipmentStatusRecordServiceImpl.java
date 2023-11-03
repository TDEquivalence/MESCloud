package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.constant.EquipmentStatus;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.RequestKpiDto;
import com.alcegory.mescloud.repository.EquipmentStatusRecordRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.EquipmentStatusRecordDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.EquipmentStatusRecordEntity;
import com.alcegory.mescloud.service.EquipmentStatusRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
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

        setActiveStatus(equipmentStatusRecord, equipmentId, equipmentStatus);

        EquipmentStatusRecordEntity persistedEquipmentStatus = repository.save(equipmentStatusRecord);
        return converter.toDto(persistedEquipmentStatus, EquipmentStatusRecordDto.class);
    }

    private void setActiveStatus(EquipmentStatusRecordEntity equipmentStatusRecord, long equipmentId, int equipmentStatus) {
        log.info(() -> String.format(" Find last active status for equipmentId: [%s]", equipmentId));
        EquipmentStatusRecordEntity lastEquipmentActiveStatus = repository.findLastEquipmentStatusWithStatusOne(equipmentId);
        if (equipmentStatus == 0 && lastEquipmentActiveStatus.getEquipmentStatus().getStatus() == 1) {
            log.info(() -> String.format("Last active status: [%s]", lastEquipmentActiveStatus));
            Timestamp lastActiveStatus = equipmentStatusRecord.getRegisteredAt();
            Timestamp previousActiveStatus = lastEquipmentActiveStatus.getRegisteredAt();

            long timeDifferenceMillis = lastActiveStatus.getTime() - previousActiveStatus.getTime();
            equipmentStatusRecord.setActiveTime(timeDifferenceMillis);
        }
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
    public Long calculateActiveTimeInSeconds(Long equipmentId,ProductionOrderDto productionOrder, Timestamp startDate, Timestamp endDate) {

        Timestamp createdAt = (Timestamp) productionOrder.getCreatedAt();
        Timestamp completedAt = (Timestamp) productionOrder.getCompletedAt();

        startDate = (startDate.before(createdAt)) ? createdAt : startDate;
        endDate = (endDate.before(createdAt)) ? createdAt : endDate;
        endDate = (completedAt != null && completedAt.before(endDate)) ? completedAt : endDate;

        return repository.sumTotalActiveTime(equipmentId, startDate, endDate);
    }
}

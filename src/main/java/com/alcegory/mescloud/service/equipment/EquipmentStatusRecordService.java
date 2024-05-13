package com.alcegory.mescloud.service.equipment;

import com.alcegory.mescloud.model.dto.EquipmentStatusRecordDto;

import java.sql.Timestamp;

public interface EquipmentStatusRecordService {

    EquipmentStatusRecordDto save(long equipmentId, int equipmentStatus);

    Long calculateStoppageTimeInSeconds(Long equipmentId, Timestamp startDate, Timestamp endDate);
}

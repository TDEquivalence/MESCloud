package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.EquipmentStatusRecordDto;

import java.sql.Timestamp;

public interface EquipmentStatusRecordService {

    EquipmentStatusRecordDto save(long equipmentId, int equipmentStatus);

    Long calculateStoppageTimeInSeconds(Long equipmentId, Timestamp startDate, Timestamp endDate);
}

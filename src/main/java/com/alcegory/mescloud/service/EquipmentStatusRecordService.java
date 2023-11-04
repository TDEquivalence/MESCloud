package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.EquipmentStatusRecordDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.RequestKpiDto;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;

import java.sql.Time;
import java.sql.Timestamp;

public interface EquipmentStatusRecordService {

    EquipmentStatusRecordDto save(long equipmentId, int equipmentStatus);

    Long calculateActiveTimeInSeconds(Long equipmentId, ProductionOrderDto productionOrder, Timestamp startDate, Timestamp endDate);
}

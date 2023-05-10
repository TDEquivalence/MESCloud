package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecord> saveProductionOrderInitialCounts(EquipmentCountsMqttDTO equipmentCountsMqttDTO);

    List<CounterRecord> save(List<CounterRecord> counterRecords);
}

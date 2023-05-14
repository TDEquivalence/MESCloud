package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecord> save(EquipmentCountsMqttDto equipmentCountsMqttDTO);

    List<CounterRecord> save(List<CounterRecord> counterRecords);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);
}

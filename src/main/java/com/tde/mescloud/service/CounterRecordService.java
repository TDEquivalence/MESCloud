package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecord> findAll();

    List<CounterRecord> findAllByCriteria(CounterRecordFilterDto request);

    List<CounterRecord> save(EquipmentCountsMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);
}

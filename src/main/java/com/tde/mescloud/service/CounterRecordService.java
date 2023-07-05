package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.model.dto.PaginatedCounterRecordsDto;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> findAll();

    List<CounterRecordDto> findLastPerProductionOrder(CounterRecordFilterDto filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilterDto filterDto);

    List<CounterRecordDto> save(EquipmentCountsMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);
}
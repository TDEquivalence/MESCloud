package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;

import java.util.List;

public interface CounterRecordService {

    PaginatedCounterRecordsDto findLastPerProductionOrder(CounterRecordFilterDto filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilterDto filterDto);

    List<CounterRecordDto> save(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);
}
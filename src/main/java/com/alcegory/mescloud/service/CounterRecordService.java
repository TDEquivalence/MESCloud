package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;

import java.sql.Timestamp;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> filterConclusionRecordsKpi(KpiFilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(CounterRecordFilter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilter filterDto);

    List<CounterRecordDto> save(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer sumValidCounterIncrement(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    Integer sumCounterIncrement(Long countingEquipmentId, Timestamp startDateFilter, Timestamp endDateFilter);

    void isProductionOrderCompleted(String productionOrderCode);
}
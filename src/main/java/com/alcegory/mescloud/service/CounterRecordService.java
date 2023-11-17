package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.model.dto.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;

import java.time.Instant;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> filterConclusionRecordsKpi(KpiFilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(CounterRecordFilter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilter filterDto);

    List<CounterRecordDto> processCounterRecord(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer sumValidCounterIncrement(Long countingEquipmentId, Instant startDateFilter, Instant endDateFilter);

    Integer sumCounterIncrement(Long countingEquipmentId, Instant startDateFilter, Instant endDateFilter);

    Integer getComputedActiveTimeByProductionOrderId(Long productionOrderId, Instant startDate, Instant endDate);
}
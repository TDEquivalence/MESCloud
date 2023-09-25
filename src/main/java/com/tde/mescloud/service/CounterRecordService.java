package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;

import java.util.Date;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> filterConclusionRecordsKpi(KpiFilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(CounterRecordFilter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilter filterDto);

    List<CounterRecordDto> save(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer calculateIncrement(Long countingEquipmentId, Date startDateFilter, Date endDateFilter);

    Integer calculateIncrementWithApprovedPO(Long countingEquipmentId, Date startDateFilter, Date endDateFilter);
}
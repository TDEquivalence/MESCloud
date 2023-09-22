package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> filterConclusionRecordsKpi(KpiFilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(CounterRecordFilter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilter filterDto);

    List<CounterRecordDto> save(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer calculateIncrement(Long countingEquipmentId);

    Integer calculateIncrementWithApprovedPO(@Param("countingEquipmentId") Long countingEquipmentId);
}
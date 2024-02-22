package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.FilterDto;
import com.alcegory.mescloud.model.dto.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.filter.Filter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> getEquipmentOutputProductionPerDay(FilterDto filter);

    List<CounterRecordDto> filterConclusionRecordsKpi(FilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(Filter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(Filter filterDto);

    void processCounterRecord(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer sumValidCounterIncrement(Long countingEquipmentId, FilterDto filter);

    Integer sumCounterIncrement(Long countingEquipmentId, FilterDto filter);

    Integer sumIncrementActiveTimeByProductionOrderId(Long productionOrderId, Long equipmentOutputId, Timestamp startDate,
                                                      Timestamp endDate);

    Instant getLastRegisteredAtByProductionOrderId(Long productionOrderId);
}
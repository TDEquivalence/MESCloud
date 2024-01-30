package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.KpiFilterDto;
import com.alcegory.mescloud.model.dto.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;

import java.sql.Timestamp;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> getEquipmentOutputProductionPerDay(KpiFilterDto filter);

    List<CounterRecordDto> filterConclusionRecordsKpi(KpiFilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(CounterRecordFilter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordFilter filterDto);

    void processCounterRecord(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer sumValidCounterIncrement(Long countingEquipmentId, KpiFilterDto filter);

    Integer sumCounterIncrement(Long countingEquipmentId, KpiFilterDto filter);

    Integer sumIncrementActiveTimeByProductionOrderId(Long productionOrderId, Long equipmentOutputId, Timestamp startDate,
                                                      Timestamp endDate);
}
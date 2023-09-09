package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDto> winnowConclusionRecordsKpi(KpiFilterDto filterDto);

    PaginatedCounterRecordsDto winnowConclusionRecordsPaginated(CounterRecordWinnowDto filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(CounterRecordWinnowDto filterDto);

    List<CounterRecordDto> save(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);
}
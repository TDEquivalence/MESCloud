package com.alcegory.mescloud.service.record;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.mqqt.PlcMqttDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.filter.FilterDto;

import java.sql.Timestamp;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordSummaryEntity> getEquipmentOutputProductionPerDay(FilterDto filter);

    List<CounterRecordDto> filterConclusionRecordsKpi(FilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(Filter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(Filter filterDto);

    void processCounterRecord(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer sumValidCounterIncrement(Long countingEquipmentId, FilterDto filter);

    Integer sumTotalCounterIncrement(Long countingEquipmentId, FilterDto filter);
    
    Timestamp getLastRegisteredAtByProductionOrderId(Long productionOrderId);

    void validateProductionOrder(String equipmentCode, String productionOrderCode);

    Long sumActiveTimeDayByProductionOrderId(Long productionOrderId, Long equipmentId, Timestamp startDate, Timestamp endDate);

    List<CounterRecordSummaryEntity> findByEquipmentAndPeriod(
            Long equipmentId, String productionOrderCode, Timestamp startDate, Timestamp endDate);
}
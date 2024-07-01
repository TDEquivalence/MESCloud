package com.alcegory.mescloud.service.record;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.mqqt.PlcMqttDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordDailySummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.filter.FilterDto;

import java.sql.Timestamp;
import java.util.List;

public interface CounterRecordService {

    List<CounterRecordDailySummaryEntity> getEquipmentOutputProductionPerDay(long sectionId, FilterDto filter);

    List<CounterRecordDto> filterConclusionRecordsKpi(long sectionId, FilterDto filterDto);

    PaginatedCounterRecordsDto filterConclusionRecordsPaginated(long sectionId, Filter filterDto);

    PaginatedCounterRecordsDto getFilteredAndPaginated(long sectionId, Filter filterDto);

    void processCounterRecord(PlcMqttDto equipmentCountsMqttDTO);

    boolean areValidInitialCounts(String productionOrderCode);

    boolean areValidContinuationCounts(String productionOrderCode);

    Integer sumValidCounterIncrement(Long sectionId, Long countingEquipmentId, FilterDto filter);

    Integer sumTotalCounterIncrement(Long sectionId, Long countingEquipmentId, FilterDto filter);

    Timestamp getLastRegisteredAtByProductionOrderId(Long productionOrderId);

    void validateProductionOrder(String equipmentCode, String productionOrderCode);

    Long sumActiveTimeDayByProductionOrderId(Long productionOrderId, Long equipmentId, Timestamp startDate, Timestamp endDate);

    List<CounterRecordDailySummaryEntity> findByEquipmentAndPeriod(Long sectionId,
                                                                   Long equipmentId, FilterDto filter);
}
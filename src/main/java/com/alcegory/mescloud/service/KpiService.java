package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.*;

import java.util.List;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);


    KpiDto computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto);

    Long getProductionOrderTotalScheduledTime(Long equipmentId, RequestKpiDto filter);

    KpiDto computeAvailability(Long equipmentId, RequestKpiDto filter);

    EquipmentKpiAggregatorDto getEquipmentKpiAggregator(Long equipmentId, RequestKpiDto requestKpiDto);

    List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(Long equipmentId, RequestKpiDto kpiRequest);
}

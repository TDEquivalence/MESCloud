package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;

import java.util.List;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);

    KpiDto computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto);

    Long getTotalScheduledTime(Long equipmentId, RequestKpiDto filter);

    KpiDto computeAvailability(Long equipmentId, RequestKpiDto filter);

    EquipmentKpiAggregatorDto getEquipmentKpiAggregator(Long equipmentId, RequestKpiDto requestKpiDto);

    List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(Long equipmentId, RequestKpiDto kpiRequest);
}

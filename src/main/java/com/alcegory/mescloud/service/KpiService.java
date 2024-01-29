package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.*;

import java.util.List;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);

    CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(KpiFilterDto filter);

    KpiDto computeEquipmentQuality(Long equipmentId, KpiFilterDto requestKpiDto);

    KpiDto computeAvailability(Long equipmentId, KpiFilterDto filter);

    EquipmentKpiAggregatorDto getEquipmentKpiAggregator(Long equipmentId, KpiFilterDto requestKpiDto);

    List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(Long equipmentId, KpiFilterDto kpiRequest);
}

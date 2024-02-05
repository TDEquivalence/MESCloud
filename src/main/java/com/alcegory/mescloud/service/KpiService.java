package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.dto.KpiDto;
import com.alcegory.mescloud.model.dto.KpiFilterDto;

import java.util.List;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter);

    CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(KpiFilterDto filter);

    KpiDto computeEquipmentQuality(Long equipmentId, KpiFilterDto requestKpiDto);

    KpiDto computeAvailability(Long equipmentId, KpiFilterDto filter);

    EquipmentKpiAggregatorDto computeEquipmentKpiAggregator(KpiFilterDto filter);

    EquipmentKpiAggregatorDto computeEquipmentKpiAggregatorById(Long equipmentId, KpiFilterDto requestKpiDto);

    List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDayById(Long equipmentId, KpiFilterDto kpiRequest);

    List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDay(KpiFilterDto filter);
}

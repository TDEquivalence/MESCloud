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

    EquipmentKpiAggregatorDto getEquipmentKpiAggregator(KpiFilterDto filter);

    EquipmentKpiAggregatorDto getEquipmentKpiAggregatorById(Long equipmentId, KpiFilterDto requestKpiDto);

    List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDayById(Long equipmentId, KpiFilterDto kpiRequest);

    List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(KpiFilterDto filter);
}

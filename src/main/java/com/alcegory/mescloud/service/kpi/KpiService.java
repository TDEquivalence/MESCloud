package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.model.dto.KpiDto;

import java.util.List;

public interface KpiService {

    CountingEquipmentKpiDto[] computeEquipmentKpi(FilterDto filter);

    CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(FilterDto filter);

    KpiDto computeEquipmentQuality(Long equipmentId, FilterDto requestKpiDto);

    KpiDto computeAvailability(Long equipmentId, FilterDto filter);

    EquipmentKpiAggregatorDto computeEquipmentKpiAggregator(FilterDto filter);

    EquipmentKpiAggregatorDto computeEquipmentKpiAggregatorById(Long equipmentId, FilterDto requestKpiDto);

    List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDayById(Long equipmentId, FilterDto kpiRequest);

    List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDay(FilterDto filter);
}

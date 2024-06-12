package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.filter.FilterDto;

import java.util.List;
import java.util.NoSuchElementException;

public interface KpiManagementService {

    CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(long sectionId, FilterDto filter);

    CountingEquipmentKpiDto[] computeEquipmentKpi(long sectionId, FilterDto filter);

    EquipmentKpiAggregatorDto computeEquipmentKpiAggregatorById(Long equipmentId, FilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException;

    List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDay(FilterDto filter);

    List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDayById(Long equipmentId, FilterDto filter);

    EquipmentKpiAggregatorDto computeAllEquipmentKpiAggregator(FilterDto filter);
}

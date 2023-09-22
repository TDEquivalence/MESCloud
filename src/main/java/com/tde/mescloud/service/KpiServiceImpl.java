package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordFilter;
import com.tde.mescloud.model.dto.CountingEquipmentKpiDto;
import com.tde.mescloud.model.dto.KpiFilterDto;
import com.tde.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class KpiServiceImpl implements KpiService {

    CounterRecordService counterRecordService;


    @Override
    public CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto kpiFilter) {

        List<CounterRecordDto> equipmentCounts = counterRecordService.filterConclusionRecordsKpi(kpiFilter);

        if (equipmentCounts.isEmpty()) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();

        Instant startDate = getPropertyAsInstant(kpiFilter, CounterRecordFilter.Property.START_DATE);
        Instant endDate = getPropertyAsInstant(kpiFilter, CounterRecordFilter.Property.END_DATE);
        //TODO: TimeMode should be applied here
        final int spanInDays = DateUtil.spanInDays(startDate, endDate);

        for (CounterRecordDto equipmentCount : equipmentCounts) {

            String equipmentAlias = equipmentCount.getEquipmentAlias();
            CountingEquipmentKpiDto equipmentKpi = equipmentKpiByEquipmentAlias.computeIfAbsent(equipmentAlias,
                    equipmentAliasKey -> new CountingEquipmentKpiDto(equipmentAliasKey, spanInDays));

            final int timeUnitAsIndex = DateUtil.differenceInDays(startDate, equipmentCount.getRegisteredAt());
            equipmentKpi.updateCounts(timeUnitAsIndex, equipmentCount);
        }

        return equipmentKpiByEquipmentAlias.values()
                .toArray(new CountingEquipmentKpiDto[equipmentKpiByEquipmentAlias.size()]);
    }

    //TODO: This should be a filter behavior
    private Instant getPropertyAsInstant(KpiFilterDto filter, CounterRecordFilter.Property counterRecordProperty) {
        return DateUtil.convertToInstant(filter.getSearch().getValue(counterRecordProperty));
    }

    @Override
    public Double computeEquipmentQualityId(Long equipmentId, KpiFilterDto kpiFilter) {
        Integer totalIncrement = counterRecordService.calculateIncrement(equipmentId);
        Integer totalIncrementWithApprovedPO = counterRecordService.calculateIncrementWithApprovedPO(equipmentId);

        if (totalIncrementWithApprovedPO == null || totalIncrement == null || totalIncrement == 0) {
            return 0.0; // Return 0.0 in case of division by zero or if totalIncrementWithApprovedPO is null.
        }

        return (double) totalIncrementWithApprovedPO / totalIncrement;
    }

}
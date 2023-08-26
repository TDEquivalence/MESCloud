package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;
import com.tde.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class KpiServiceImpl implements KpiService {

    CounterRecordService counterRecordService;


    @Override
    public CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto kpiFilter) {

        PaginatedCounterRecordsDto equipmentCounts = counterRecordService.findLastPerProductionOrder(kpiFilter);

        if (!hasCounterRecords(equipmentCounts)) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();

        Date startDate = getPropertyAsDate(kpiFilter, CounterRecordFilterDto.CounterRecordProperty.START_DATE);
        Date endDate = getPropertyAsDate(kpiFilter, CounterRecordFilterDto.CounterRecordProperty.END_DATE);
        //TODO: TimeMode should be applied here
        final int spanInDays = DateUtil.spanInDays(startDate, endDate);

        for (CounterRecordDto equipmentCount : equipmentCounts.getCounterRecords()) {

            String equipmentAlias = equipmentCount.getEquipmentAlias();

            CountingEquipmentKpiDto equipmentKpi = equipmentKpiByEquipmentAlias.get(equipmentAlias);

            if (equipmentKpi == null) {
                equipmentKpi = new CountingEquipmentKpiDto(equipmentAlias, spanInDays);
                equipmentKpiByEquipmentAlias.put(equipmentAlias, equipmentKpi);
            }

            final int timeUnitAsIndex = DateUtil.differenceInDays(startDate, equipmentCount.getRegisteredAt());
            equipmentKpi.updateCounts(timeUnitAsIndex, equipmentCount);
        }

        return equipmentKpiByEquipmentAlias.values()
                .toArray(new CountingEquipmentKpiDto[equipmentKpiByEquipmentAlias.size()]);
    }

    private Date getPropertyAsDate(KpiFilterDto filter, CounterRecordFilterDto.CounterRecordProperty counterRecordProperty) {
        return DateUtil.convertToDate(filter.getSearch().getValue(counterRecordProperty));
    }

    private boolean hasCounterRecords(PaginatedCounterRecordsDto paginatedCounterRecords) {
        return paginatedCounterRecords.getCounterRecords() != null &&
                !paginatedCounterRecords.getCounterRecords().isEmpty();
    }
}
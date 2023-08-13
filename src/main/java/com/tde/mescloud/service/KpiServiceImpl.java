package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;
import com.tde.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class KpiServiceImpl implements KpiService {

    private final String NOT_VALID_OUTPUT = "NOTOK";
    private final String VALID_OUTPUT = "OK";

    CounterRecordService counterRecordService;


    @Override
    public CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto kpiFilter) {

        CounterRecordFilterDto counterRecordFilter = convertToCounterRecordFilter(kpiFilter);
        PaginatedCounterRecordsDto equipmentCounts = counterRecordService.findLastPerProductionOrder(counterRecordFilter);

        if (!hasCounterRecords(equipmentCounts)) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();

        Date startDate = DateUtil.convertToDate(kpiFilter.getStartDate());
        Date endDate = DateUtil.convertToDate(kpiFilter.getEndDate());
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

    //TODO: To be removed - KpiFilter and CounterRecordFilter should become one and the same <3
    private CounterRecordFilterDto convertToCounterRecordFilter(KpiFilterDto kpiFilter) {
        CounterRecordFilterDto counterRecordFilter = new CounterRecordFilterDto();
        counterRecordFilter.setTake(1000);
        counterRecordFilter.setSkip(0);

        CounterRecordSearchDto dateStart = new CounterRecordSearchDto();
        dateStart.setId("dateStart");
        dateStart.setValue(kpiFilter.getStartDate());

        CounterRecordSearchDto dateEnd = new CounterRecordSearchDto();
        dateEnd.setId("dateEnd");
        dateEnd.setValue(kpiFilter.getEndDate());


        if (kpiFilter.getSearch() == null) {
            counterRecordFilter.setSearch(new CounterRecordSearchDto[0]);
        } else {
            List<CounterRecordSearchDto> searchesAsList = new ArrayList<>(Arrays.stream(kpiFilter.getSearch()).toList());
            searchesAsList.add(dateStart);
            searchesAsList.add(dateEnd);
            counterRecordFilter.setSearch(searchesAsList.toArray(new CounterRecordSearchDto[0]));
        }

        counterRecordFilter.setSort(new CounterRecordSortDto[0]);

        return counterRecordFilter;
    }

    private boolean hasCounterRecords(PaginatedCounterRecordsDto paginatedCounterRecords) {
        return paginatedCounterRecords.getCounterRecords() != null &&
                paginatedCounterRecords.getCounterRecords().size() > 0;
    }
}
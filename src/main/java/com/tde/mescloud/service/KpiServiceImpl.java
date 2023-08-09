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

    private final String NOT_VALID_OUTPUT = "NOTOK";
    private final String VALID_OUTPUT = "OK";

    CounterRecordService counterRecordService;

    @Override
    public CountingEquipmentKpiDto[] getCountingEquipmentKpi(KpiFilterDto filter) {

        CounterRecordFilterDto counterRecordFilterDto = new CounterRecordFilterDto();
        counterRecordFilterDto.setTake(1000);
        counterRecordFilterDto.setSkip(0);

        if (filter.getSearch() == null) {
            counterRecordFilterDto.setSearch(new CounterRecordSearchDto[0]);
        } else {
            counterRecordFilterDto.setSearch(filter.getSearch());
        }

        counterRecordFilterDto.setSort(new CounterRecordSortDto[0]);

        PaginatedCounterRecordsDto conclusionCounterRecords =
                counterRecordService.findLastPerProductionOrder(counterRecordFilterDto);

        if (conclusionCounterRecords.getCounterRecords().size() < 1) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();
        final long daysInBetween = DateUtil.calculateDaysBetween(filter.getStartDate(), filter.getEndDate());
        Date lastComputedDate = null;
        int currentIndex = 0;

        for (CounterRecordDto conclusionCounterRecord : conclusionCounterRecords.getCounterRecords()) {

            String equipmentAlias = conclusionCounterRecord.getEquipmentAlias();
            CountingEquipmentKpiDto equipmentKpi = equipmentKpiByEquipmentAlias.get(equipmentAlias);

            if (equipmentKpi == null) {
                equipmentKpi = new CountingEquipmentKpiDto(equipmentAlias, (int) daysInBetween);
                equipmentKpiByEquipmentAlias.put(equipmentAlias, equipmentKpi);
            }

            if (lastComputedDate == null) {
                lastComputedDate = conclusionCounterRecord.getRegisteredAt();
            }

            //TODO: change to isDifferentDay? which checks FIRST if isDayBefore
            if (DateUtil.isDayBefore(conclusionCounterRecord.getRegisteredAt(), lastComputedDate)) {
                lastComputedDate = conclusionCounterRecord.getRegisteredAt();
                //TODO: Remove comment after testing
                //Is any of the found machines empty? If so, set valid & invalidCounts with 0 at currentIndex before increment
                //Actually, this is already done. int[] default value is 0. If no value is worked...
                currentIndex++;
            }

            //TODO: Change constants by boolean check
            if (NOT_VALID_OUTPUT.equals(conclusionCounterRecord.getEquipmentOutputAlias())) {
                equipmentKpi.getInvalidCounts()[currentIndex] += conclusionCounterRecord.getComputedValue();
            }

            if (VALID_OUTPUT.equals(conclusionCounterRecord.getEquipmentOutputAlias())) {
                equipmentKpi.getValidCounts()[currentIndex] += conclusionCounterRecord.getComputedValue();
            }
        }

        return equipmentKpiByEquipmentAlias.values().toArray(new CountingEquipmentKpiDto[equipmentKpiByEquipmentAlias.size()]);
    }
}

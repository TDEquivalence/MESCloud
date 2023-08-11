package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;
import com.tde.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        CounterRecordSearchDto dateStart = new CounterRecordSearchDto();
        dateStart.setId("dateStart");
        dateStart.setValue(filter.getStartDate());

        CounterRecordSearchDto dateEnd = new CounterRecordSearchDto();
        dateEnd.setId("dateEnd");
        dateEnd.setValue(filter.getEndDate());


        if (filter.getSearch() == null) {
            counterRecordFilterDto.setSearch(new CounterRecordSearchDto[0]);
        } else {
            List<CounterRecordSearchDto> searchesAsList = new ArrayList<>(Arrays.stream(filter.getSearch()).toList());
            searchesAsList.add(dateStart);
            searchesAsList.add(dateEnd);
            counterRecordFilterDto.setSearch(searchesAsList.toArray(new CounterRecordSearchDto[0]));
        }

        counterRecordFilterDto.setSort(new CounterRecordSortDto[0]);

        PaginatedCounterRecordsDto conclusionCounterRecords =
                counterRecordService.findLastPerProductionOrder(counterRecordFilterDto);

        if (conclusionCounterRecords.getCounterRecords().size() < 1) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant startInstant = Instant.from(formatter.parse(filter.getStartDate()));
        Date startDate = Date.from(startInstant);
        Instant endInstand = Instant.from(formatter.parse(filter.getEndDate()));
        Date endDate = Date.from(endInstand);

        final int daysInBetween = (int) DateUtil.spanInDays(startDate, endDate);//5
        Date lastComputedDate = endDate;
        int currentIndex = daysInBetween - 1;

        for (CounterRecordDto conclusionCounterRecord : conclusionCounterRecords.getCounterRecords()) {

            String equipmentAlias = conclusionCounterRecord.getEquipmentAlias();
            CountingEquipmentKpiDto equipmentKpi = equipmentKpiByEquipmentAlias.get(equipmentAlias);

            if (equipmentKpi == null) {
                equipmentKpi = new CountingEquipmentKpiDto(equipmentAlias, (int) daysInBetween);
                equipmentKpiByEquipmentAlias.put(equipmentAlias, equipmentKpi);
            }

            //TODO: change to isDifferentDay? which checks FIRST if isDayBefore
            if (DateUtil.isDayBefore(conclusionCounterRecord.getRegisteredAt(), lastComputedDate)) {
                Date currentDate = conclusionCounterRecord.getRegisteredAt();//18, last computed = 20
                final int intermediateDaysInBetween = (int) DateUtil.differenceInDays(currentDate, lastComputedDate); //2
                currentIndex -= intermediateDaysInBetween;
                lastComputedDate = currentDate;
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

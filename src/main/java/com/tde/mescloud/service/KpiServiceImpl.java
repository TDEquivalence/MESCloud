package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;
import com.tde.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class KpiServiceImpl implements KpiService {

    CounterRecordService counterRecordService;
    ProductionOrderService productionOrderService;
    EquipmentStatusRecordService equipmentStatusRecordService;

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
    public KpiAvailabilityDto getAvailability(RequestEquipmentKpiDto filter) {
        Long totalScheduledTime = getTotalScheduledTime(filter);
        Long totalStoppageTime = getTotalStoppageTime(filter);

        KpiAvailabilityDto kpiAvailability = new KpiAvailabilityDto();
        kpiAvailability.setScheduledTime(totalScheduledTime);
        kpiAvailability.setEffectiveTime(totalScheduledTime - (double) totalStoppageTime);
        kpiAvailability.setAvailabilityPercentage(calculateAvailabilityPercentage(totalScheduledTime, totalStoppageTime));

        return kpiAvailability;
    }

    public Long getTotalScheduledTime(RequestEquipmentKpiDto filter) {
        return productionOrderService.calculateScheduledTimeInSeconds(
                filter.getEquipmentId(),
                filter.getStartDate(),
                filter.getEndDate());
    }

    private double calculateAvailabilityPercentage(long totalScheduledTime, long totalStoppageTime) {
        return (double) (totalStoppageTime * 100) / totalScheduledTime;
    }

    private Long getTotalStoppageTime(RequestEquipmentKpiDto filter) {
        Long equipmentId = filter.getEquipmentId();
        Date startDate = filter.getStartDate();
        Date endDate = filter.getEndDate();

        List<ProductionOrderDto> productionOrders =
                productionOrderService.findByEquipmentAndPeriod(equipmentId, startDate, endDate);

        Long totalStoppageTime = 0L;
        for (ProductionOrderDto productionOrder : productionOrders) {
            totalStoppageTime +=
                    equipmentStatusRecordService.calculateStoppageTimeInSeconds(
                            equipmentId,
                            Timestamp.from(productionOrder.getCreatedAt().toInstant()),
                            Timestamp.from(productionOrder.getCompletedAt().toInstant()));
        }

        return totalStoppageTime;
    }

//    public Long getTotalStoppageTime(RequestEquipmentKpiDto filter) {
//        return equipmentStatusRecordService.calculateStoppageTimeInSeconds(
//                filter.getEquipmentId(),
//                Timestamp.from(filter.getStartDate().toInstant()),
//                Timestamp.from(filter.getEndDate().toInstant()));
//    }

    @Override
    public Double computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto) {
        Integer totalIncrement = counterRecordService.calculateIncrement(equipmentId, requestKpiDto.getStartDate(), requestKpiDto.getEndDate());
        Integer totalIncrementWithApprovedPO = counterRecordService.calculateIncrementWithApprovedPO(equipmentId, requestKpiDto.getStartDate(), requestKpiDto.getEndDate());

        if (totalIncrementWithApprovedPO == null || totalIncrement == null || totalIncrement == 0) {
            return 0.0;
        }

        return (double) totalIncrementWithApprovedPO / totalIncrement;
    }
}
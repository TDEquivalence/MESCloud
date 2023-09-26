package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.*;
import com.tde.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
@Log
public class KpiServiceImpl implements KpiService {

    private static final int PERCENTAGE = 100;

    private final CounterRecordService counterRecordService;
    private final ProductionOrderService productionOrderService;
    private final EquipmentStatusRecordService equipmentStatusRecordService;
    private final CountingEquipmentService countingEquipmentService;

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
    public KpiDto computeAvailability(Long equipmentId, RequestKpiDto filter) {
        double totalScheduledTime = getTotalScheduledTime(equipmentId, filter);
        double totalStoppageTime = getTotalStoppageTime(equipmentId, filter);

        return createKpi(totalScheduledTime, totalStoppageTime);
    }

    public Long getTotalScheduledTime(Long equipmentId, RequestKpiDto filter) {
        return productionOrderService.calculateScheduledTimeInSeconds(
                equipmentId,
                filter.getStartDate(),
                filter.getEndDate());
    }

    private Long getTotalStoppageTime(Long equipmentId, RequestKpiDto filter) {
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

    @Override
    public KpiDto computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto) {
        double validCounter = counterRecordService.sumValidCounterIncrement(equipmentId, requestKpiDto.getStartDate(), requestKpiDto.getEndDate());
        double totalCounter = counterRecordService.sumCounterIncrement(equipmentId, requestKpiDto.getStartDate(), requestKpiDto.getEndDate());

        return createKpi(validCounter, totalCounter);
    }

    private KpiDto createKpi(double dividend, double divider) {
        KpiDto kpi = new KpiDto();
        kpi.setDividend(dividend);
        kpi.setDivider(divider);
        kpi.setValue(dividend / divider);

        return kpi;
    }

    private KpiDto computePerformance(KpiDto qualityKpi, KpiDto availabilityKpi, CountingEquipmentDto countingEquipment) {
        double realProductionInSeconds = qualityKpi.getDividend() / availabilityKpi.getDividend();
        return createKpi(realProductionInSeconds, countingEquipment.getTheoreticalProduction());
    }

    private double computeOverallEffectivePerformance(KpiDto quality, KpiDto availability, KpiDto performance) {
        return quality.getValue() * availability.getValue() * performance.getValue();
    }

    @Override
    public EquipmentKpiAggregatorDto getEquipmentKpiAggregator(Long equipmentId, RequestKpiDto requestKpiDto) throws NoSuchElementException {

        Optional<CountingEquipmentDto> countingEquipmentDtoOpt = countingEquipmentService.findById(equipmentId);
        if (countingEquipmentDtoOpt.isEmpty()) {
            String msg = String.format("Unable to find counting equipment with id [%s]", equipmentId);
            log.warning(msg);
            throw new NoSuchElementException(msg);
        }

        CountingEquipmentDto countingEquipment = countingEquipmentDtoOpt.get();

        KpiDto qualityKpi = computeEquipmentQuality(equipmentId, requestKpiDto);
        KpiDto availabilityKpi = computeAvailability(equipmentId, requestKpiDto);
        KpiDto performanceKpi = computePerformance(qualityKpi, availabilityKpi, countingEquipment);
        double overallEffectivePerformance = computeOverallEffectivePerformance(qualityKpi, availabilityKpi, performanceKpi);

        EquipmentKpiDto quality = createEquipmentKpi(countingEquipment.getQualityTarget(), qualityKpi.getValue());
        EquipmentKpiDto availability = createEquipmentKpi(countingEquipment.getAvailabilityTarget(), availabilityKpi.getValue());
        EquipmentKpiDto performance = createEquipmentKpi(countingEquipment.getPerformanceTarget(), performanceKpi.getValue());
        EquipmentKpiDto overallEquipmentEffectiveness = createEquipmentKpi(countingEquipment.getOverallEquipmentEffectivenessTarget(), overallEffectivePerformance);

        return EquipmentKpiAggregatorDto.builder()
                .qualityKpi(quality)
                .availabilityKpi(availability)
                .performanceKpi(performance)
                .overallEffectiveness(overallEquipmentEffectiveness)
                .build();
    }

    private EquipmentKpiDto createEquipmentKpi(double target, double value) {
        EquipmentKpiDto equipmentKpi = new EquipmentKpiDto();
        equipmentKpi.setKpiTarget(target);
        equipmentKpi.setKpiValue(value);

        return equipmentKpi;
    }
}
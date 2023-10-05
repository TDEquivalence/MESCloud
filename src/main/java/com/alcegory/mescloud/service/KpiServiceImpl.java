package com.alcegory.mescloud.service;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.utility.DateUtil;
import com.alcegory.mescloud.utility.DoubleUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
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

    @Override
    public EquipmentKpiAggregatorDto getEquipmentKpiAggregator(Long equipmentId, RequestKpiDto requestKpiDto)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {

        Optional<CountingEquipmentDto> countingEquipmentDtoOpt = countingEquipmentService.findById(equipmentId);
        if (countingEquipmentDtoOpt.isEmpty()) {
            String msg = String.format("Unable to find counting equipment with id [%s]", equipmentId);
            log.warning(msg);
            throw new NoSuchElementException(msg);
        }

        CountingEquipmentDto countingEquipment = countingEquipmentDtoOpt.get();

        KpiDto qualityKpi = computeEquipmentQuality(equipmentId, requestKpiDto);
        EquipmentKpiDto quality = new EquipmentKpiDto(countingEquipment, qualityKpi);

        KpiDto availabilityKpi = computeAvailability(equipmentId, requestKpiDto);
        EquipmentKpiDto availability = new EquipmentKpiDto(countingEquipment, availabilityKpi);

        KpiDto performanceKpi = computePerformance(qualityKpi, availabilityKpi, countingEquipment);
        EquipmentKpiDto performance = new EquipmentKpiDto(countingEquipment, performanceKpi);

        Double overallEffectivePerformance = computeOverallEffectivePerformance(qualityKpi, availabilityKpi, performanceKpi);
        EquipmentKpiDto overallEquipmentEffectiveness = new EquipmentKpiDto(countingEquipment, overallEffectivePerformance);

        return EquipmentKpiAggregatorDto.builder()
                .qualityKpi(quality)
                .availabilityKpi(availability)
                .performanceKpi(performance)
                .overallEquipmentEffectivenessKpi(overallEquipmentEffectiveness)
                .build();
    }

    @Override
    public List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(Long equipmentId, RequestKpiDto kpiRequest) {

        final Instant startDate = kpiRequest.getStartDate().toInstant();
        final Instant endDate = kpiRequest.getEndDate().toInstant();

        final int spanInDays = DateUtil.spanInDays(startDate, endDate);
        List<EquipmentKpiAggregatorDto> equipmentKpiAggregators = new ArrayList<>();

        for (int i = 0; i <= spanInDays; i++) {
            Instant dailyStartDate = startDate.plus(Duration.ofDays(i));
            Instant dailyEndDate = dailyStartDate.plus(Duration.ofHours(23).plusMinutes(59));

            RequestKpiDto dailyRequest = new RequestKpiDto();
            dailyRequest.setStartDate(Timestamp.from(dailyStartDate));
            dailyRequest.setEndDate(Timestamp.from(dailyEndDate));

            EquipmentKpiAggregatorDto aggregator = getEquipmentKpiAggregator(equipmentId, dailyRequest);
            equipmentKpiAggregators.add(aggregator);
        }

        return equipmentKpiAggregators;
    }

    private Instant getPropertyAsInstant(KpiFilterDto filter, CounterRecordFilter.Property counterRecordProperty) {
        return DateUtil.convertToInstant(filter.getSearch().getValue(counterRecordProperty));
    }

    @Override
    public KpiDto computeAvailability(Long equipmentId, RequestKpiDto filter) {
        Long totalScheduledTime = getTotalScheduledTime(equipmentId, filter);
        Long totalStoppageTime = getTotalStoppageTime(equipmentId, filter);

        return new KpiDto(DoubleUtil.safeDoubleValue(totalScheduledTime), DoubleUtil.safeDoubleValue(totalStoppageTime));
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
        Integer validCounter = counterRecordService.sumValidCounterIncrement(equipmentId, requestKpiDto.getStartDate(), requestKpiDto.getEndDate());
        Integer totalCounter = counterRecordService.sumCounterIncrement(equipmentId, requestKpiDto.getStartDate(), requestKpiDto.getEndDate());

        return new KpiDto(validCounter, totalCounter);
    }

    private KpiDto computePerformance(KpiDto qualityKpi, KpiDto availabilityKpi, CountingEquipmentDto countingEquipment) {

        if (countingEquipment.getTheoreticalProduction() == null) {
            log.warning(String.format("Unable to compute performance: equipment [%s] has no theoretical production configuration value", countingEquipment.getId()));
            return null;
        }

        if (qualityKpi.getDividend() == 0 || availabilityKpi.getDividend() == 0) {
            log.warning(String.format("Unable to compute performance: cannot divide quality dividend [%s] by the availability dividend [%s]", qualityKpi.getDividend(), availabilityKpi.getDividend()));
            return null;
        }

        Double realProductionInSeconds = qualityKpi.getDividend() / availabilityKpi.getDividend();
        return new KpiDto(realProductionInSeconds, DoubleUtil.safeDoubleValue(countingEquipment.getTheoreticalProduction()));
    }

    private Double computeOverallEffectivePerformance(KpiDto quality, KpiDto availability, KpiDto performance) {

        if (isValueZeroOrMissing(quality) || isValueZeroOrMissing(availability) || isValueZeroOrMissing(performance)) {
            return null;
        }

        return quality.getValue() * availability.getValue() * performance.getValue();
    }

    private boolean isValueZeroOrMissing(KpiDto kpiDto) {
        return kpiDto == null || kpiDto.getValue() == null || kpiDto.getValue() == 0;
    }
}
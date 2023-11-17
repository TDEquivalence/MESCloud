package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;
import com.alcegory.mescloud.service.CounterRecordService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.service.KpiService;
import com.alcegory.mescloud.service.ProductionOrderService;
import com.alcegory.mescloud.utility.DateUtil;
import com.alcegory.mescloud.utility.DoubleUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static com.alcegory.mescloud.model.dto.RequestKpiDto.createRequestKpiForDay;

@Service
@AllArgsConstructor
@Log
public class KpiServiceImpl implements KpiService {

    private final CounterRecordService counterRecordService;
    private final ProductionOrderService productionOrderService;
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
        EquipmentKpiDto quality = new EquipmentKpiDto(countingEquipment.getQualityTarget(), qualityKpi);

        KpiDto availabilityKpi = computeAvailability(equipmentId, requestKpiDto);
        EquipmentKpiDto availability = new EquipmentKpiDto(countingEquipment.getAvailabilityTarget(), availabilityKpi);

        KpiDto performanceKpi = computePerformance(qualityKpi, availabilityKpi, countingEquipment);
        EquipmentKpiDto performance = new EquipmentKpiDto(countingEquipment.getPerformanceTarget(), performanceKpi);

        Double overallEffectivePerformance = computeOverallEffectivePerformance(qualityKpi, availabilityKpi, performanceKpi);
        EquipmentKpiDto overallEquipmentEffectiveness =
                new EquipmentKpiDto(countingEquipment.getOverallEquipmentEffectivenessTarget(), overallEffectivePerformance);

        return EquipmentKpiAggregatorDto.builder()
                .qualityKpi(quality)
                .availabilityKpi(availability)
                .performanceKpi(performance)
                .overallEquipmentEffectivenessKpi(overallEquipmentEffectiveness)
                .build();
    }

    @Override
    public List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(Long equipmentId, RequestKpiDto kpiRequest) {
        LocalDate startDate = kpiRequest.getStartDateAsLocalDate();
        LocalDate endDate = kpiRequest.getEndDateAsLocalDate();

        List<EquipmentKpiAggregatorDto> equipmentKpiAggregators = new ArrayList<>();

        for (LocalDate currentDay = startDate; !currentDay.isAfter(endDate); currentDay = currentDay.plusDays(1)) {
            RequestKpiDto currentKpiRequest = createRequestKpiForDay(currentDay);

            EquipmentKpiAggregatorDto aggregator = getEquipmentKpiAggregator(equipmentId, currentKpiRequest);
            equipmentKpiAggregators.add(aggregator);
        }

        return equipmentKpiAggregators;
    }

    private Instant getPropertyAsInstant(KpiFilterDto filter, CounterRecordFilter.Property counterRecordProperty) {
        return DateUtil.convertToInstant(filter.getSearch().getValue(counterRecordProperty));
    }

    @Override
    public KpiDto computeAvailability(Long equipmentId, RequestKpiDto filter) {

        List<ProductionOrderEntity> productionOrders = getProductionOrderWithinDateRange(equipmentId, filter.getStartDate(),
                filter.getEndDate());

        Long totalScheduledTime = 0L;
        Long totalActiveTime = 0L;

        for (ProductionOrderEntity productionOrder : productionOrders) {
            Instant adjustedStartDate = productionOrderService.getAdjustedStartDate(productionOrder, filter.getStartDate());
            Instant adjustedEndDate = productionOrderService.getAdjustedEndDate(productionOrder, filter.getEndDate());

            totalScheduledTime += getProductionOrderTotalScheduledTime(adjustedStartDate, adjustedEndDate);
            totalActiveTime += calculateActiveTimeByProductionOrderId(productionOrder, totalScheduledTime,
                    adjustedStartDate, adjustedEndDate);
        }

        log.info(String.format("Total schedule time [%s]", totalScheduledTime));
        log.info(String.format("Total active time [%s]", totalScheduledTime));

        KpiDto kpi = new KpiDto(DoubleUtil.safeDoubleValue(totalActiveTime), DoubleUtil.safeDoubleValue(totalScheduledTime));
        kpi.setValueAsDivision();
        return kpi;
    }

    @Override
    public Long getProductionOrderTotalScheduledTime(Long equipmentId, RequestKpiDto filter) {
        return productionOrderService.calculateScheduledTimeInSeconds(equipmentId, filter.getStartDateInstant(),
                filter.getEndDateInstant());
    }

    private Long getProductionOrderTotalScheduledTime(Instant startDate, Instant endDate) {
        return productionOrderService.calculateScheduledTimeInSeconds(startDate, endDate);
    }

    private Long calculateActiveTimeByProductionOrderId(ProductionOrderEntity productionOrder, long totalScheduledTime,
                                                        Instant startDateFilter,
                                                        Instant endDateFilter) {

        Timestamp startDate = Timestamp.from(startDateFilter);
        Timestamp endDate = Timestamp.from(endDateFilter);

        return counterRecordService.calculateActiveTimeByProductionOrderId(productionOrder.getId(), totalScheduledTime,
                startDate, endDate);
    }

    @Override
    public KpiDto computeEquipmentQuality(Long equipmentId, RequestKpiDto requestKpiDto) {
        Integer validCounter = counterRecordService.sumValidCounterIncrement(equipmentId,
                requestKpiDto.getStartDate(), requestKpiDto.getEndDate());

        Integer totalCounter = counterRecordService.sumCounterIncrement(equipmentId, requestKpiDto.getStartDate(),
                requestKpiDto.getEndDate());

        KpiDto kpi = new KpiDto(validCounter, totalCounter);
        kpi.setValueAsDivision();
        return kpi;
    }

    private KpiDto computePerformance(KpiDto qualityKpi, KpiDto availabilityKpi, CountingEquipmentDto countingEquipment) {

        if (countingEquipment.getTheoreticalProduction() == null) {
            log.warning(String.format("Unable to compute performance: equipment [%s] has no theoretical production configuration value",
                    countingEquipment.getId()));
            return null;
        }

        if (qualityKpi.getDivider() == 0 || availabilityKpi.getDividend() == 0) {
            log.warning(String.format("Unable to compute performance: cannot divide quality dividend [%s] by the availability dividend [%s]",
                    qualityKpi.getDividend(), availabilityKpi.getDividend()));
            return null;
        }

        Double realProductionInSeconds = qualityKpi.getDivider() / availabilityKpi.getDividend();
        KpiDto kpi = new KpiDto(realProductionInSeconds, countingEquipment.getTheoreticalProduction());
        kpi.setValueAsDivision();
        return kpi;
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

    private List<ProductionOrderEntity> getProductionOrderWithinDateRange(Long equipmentId, Timestamp startDateFilter,
                                                                          Timestamp endDateFilter) {

        return productionOrderService.findProductionOrdersWithinDateRange(equipmentId, startDateFilter, endDateFilter);
    }
}
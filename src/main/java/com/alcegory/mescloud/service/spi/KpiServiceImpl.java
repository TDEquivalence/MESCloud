package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.CounterRecordFilter;
import com.alcegory.mescloud.service.*;
import com.alcegory.mescloud.utility.DateUtil;
import com.alcegory.mescloud.utility.DoubleUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.alcegory.mescloud.model.filter.CounterRecordFilter.Property.*;

@Service
@AllArgsConstructor
@Log
public class KpiServiceImpl implements KpiService {

    private final CounterRecordService counterRecordService;
    private final ProductionOrderService productionOrderService;
    private final CountingEquipmentService countingEquipmentService;
    private final EquipmentOutputService equipmentOutputService;


    @Override
    public CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(KpiFilterDto filter) {
        List<CounterRecordDto> equipmentCounts = counterRecordService.getEquipmentOutputProductionPerDay(filter);
        return sortPerDay(filter, equipmentCounts);
    }

    @Override
    public CountingEquipmentKpiDto[] computeEquipmentKpi(KpiFilterDto filter) {
        List<CounterRecordDto> equipmentCounts = counterRecordService.filterConclusionRecordsKpi(filter);
        return sortPerDay(filter, equipmentCounts);
    }

    private CountingEquipmentKpiDto[] sortPerDay(KpiFilterDto filter, List<CounterRecordDto> equipmentCounts) {
        if (equipmentCounts.isEmpty()) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();

        Instant startDate = getPropertyAsInstant(filter, START_DATE);
        Instant endDate = getPropertyAsInstant(filter, END_DATE);

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

    public EquipmentKpiAggregatorDto getAllEquipmentKpiAggregator(KpiFilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {

        String equipmentAlias = filter.getSearch().getValue(EQUIPMENT_ALIAS);
        Long equipmentId = (equipmentAlias != null && !equipmentAlias.isEmpty())
                ? countingEquipmentService.findIdByAlias(equipmentAlias)
                : null;

        List<EquipmentKpiAggregatorDto> equipmentKpiAggregator = Optional.ofNullable(equipmentId)
                .map(Collections::singletonList)
                .orElseGet(countingEquipmentService::findAllIds)
                .stream()
                .map(id -> getEquipmentKpiAggregator(id, filter))
                .toList();

        return sumEquipmentKpiAggregators(equipmentKpiAggregator);
    }

    @Override
    public EquipmentKpiAggregatorDto getEquipmentKpiAggregator(Long equipmentId, KpiFilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {

        Optional<CountingEquipmentDto> countingEquipmentDtoOpt = countingEquipmentService.findById(equipmentId);
        if (countingEquipmentDtoOpt.isEmpty()) {
            String msg = String.format("Unable to find counting equipment with id [%s]", equipmentId);
            log.warning(msg);
            throw new NoSuchElementException(msg);
        }

        CountingEquipmentDto countingEquipment = countingEquipmentDtoOpt.get();

        KpiDto qualityKpi = computeEquipmentQuality(equipmentId, filter);
        EquipmentKpiDto quality = new EquipmentKpiDto(countingEquipment.getQualityTarget(), qualityKpi);

        KpiDto availabilityKpi = computeAvailability(equipmentId, filter);
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
    public List<EquipmentKpiAggregatorDto> getEquipmentKpiAggregatorPerDay(Long equipmentId, KpiFilterDto filter) {
        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<EquipmentKpiAggregatorDto> equipmentKpiAggregators = new ArrayList<>();

        LocalDateTime startLocalDateTime = startDate.toLocalDateTime();
        LocalDateTime endLocalDateTime = endDate.toLocalDateTime();

        for (LocalDateTime currentDateTime = startLocalDateTime; !currentDateTime.isAfter(endLocalDateTime); currentDateTime = currentDateTime.plusDays(1)) {
            LocalDate currentDay = currentDateTime.toLocalDate();
            String startDateFilter = currentDay.atStartOfDay().format(formatter);
            String endDateTimeFilter = currentDay.plusDays(1).atStartOfDay().minusNanos(1).format(formatter);

            filter.getSearch().setSearchValueByName(START_DATE, startDateFilter);
            filter.getSearch().setSearchValueByName(END_DATE, endDateTimeFilter);

            EquipmentKpiAggregatorDto aggregator = getEquipmentKpiAggregator(equipmentId, filter);
            equipmentKpiAggregators.add(aggregator);
        }

        return equipmentKpiAggregators;
    }

    private Instant getPropertyAsInstant(KpiFilterDto filter, CounterRecordFilter.Property counterRecordProperty) {
        return DateUtil.convertToInstant(filter.getSearch().getValue(counterRecordProperty));
    }

    @Override
    public KpiDto computeAvailability(Long equipmentId, KpiFilterDto filter) {
        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);
        String productionOrderCode = filter.getSearch().getValue(PRODUCTION_ORDER_CODE);

        List<ProductionOrderEntity> productionOrders = findByEquipmentAndPeriod(equipmentId, productionOrderCode, startDate, endDate);
        Long equipmentOutputId = equipmentOutputService.findIdByCountingEquipmentId(equipmentId);

        long totalScheduledTime = 0L;
        long totalActiveTime = 0L;

        for (ProductionOrderEntity productionOrder : productionOrders) {
            Timestamp adjustedStartDate = productionOrderService.getAdjustedStartDate(productionOrder, startDate);
            Timestamp adjustedEndDate = productionOrderService.getAdjustedEndDate(productionOrder, endDate);

            totalScheduledTime += calculateProductionOrderTotalScheduledTime(adjustedStartDate, adjustedEndDate);
            totalActiveTime += calculateActiveTimeByProductionOrderId(productionOrder, equipmentOutputId, adjustedStartDate, adjustedEndDate);
        }

        KpiDto kpi = new KpiDto(DoubleUtil.safeDoubleValue(totalActiveTime), DoubleUtil.safeDoubleValue(totalScheduledTime));
        kpi.setValueAsDivision();
        return kpi;
    }

    private Long calculateProductionOrderTotalScheduledTime(Timestamp startDate, Timestamp endDate) {
        return productionOrderService.calculateScheduledTimeInSeconds(startDate, endDate);
    }

    private Integer calculateActiveTimeByProductionOrderId(ProductionOrderEntity productionOrder, Long equipmentOutputId,
                                                           Timestamp startDate, Timestamp endDate) {
        return counterRecordService.sumIncrementActiveTimeByProductionOrderId(productionOrder.getId(), equipmentOutputId,
                startDate, endDate);
    }

    @Override
    public KpiDto computeEquipmentQuality(Long equipmentId, KpiFilterDto filter) {

        Integer validCounter = counterRecordService.sumValidCounterIncrement(equipmentId, filter);
        Integer totalCounter = counterRecordService.sumCounterIncrement(equipmentId, filter);

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

    private List<ProductionOrderEntity> findByEquipmentAndPeriod(Long equipmentId, String productionOrderCode,
                                                                 Timestamp startDateFilter, Timestamp endDateFilter) {

        return productionOrderService.findByEquipmentAndPeriod(equipmentId, productionOrderCode, startDateFilter, endDateFilter);
    }

    public EquipmentKpiAggregatorDto sumEquipmentKpiAggregators(List<EquipmentKpiAggregatorDto> aggregatorList) {
        if (aggregatorList == null || aggregatorList.isEmpty()) {
            return null;
        }

        if (aggregatorList.size() == 1) {
            return aggregatorList.get(0);
        }

        EquipmentKpiAggregatorDto result = initializeResultAggregator();

        for (EquipmentKpiAggregatorDto aggregator : aggregatorList) {
            sumEquipmentKpiDto(result.getQualityKpi(), aggregator.getQualityKpi());
            sumEquipmentKpiDto(result.getAvailabilityKpi(), aggregator.getAvailabilityKpi());
            sumEquipmentKpiDto(result.getPerformanceKpi(), aggregator.getPerformanceKpi());
            sumEquipmentKpiDto(result.getOverallEquipmentEffectivenessKpi(), aggregator.getOverallEquipmentEffectivenessKpi());
        }

        updateTargets(result, aggregatorList.size());
        calculateOverallEquipmentEffectiveness(result);
        return result;
    }

    private EquipmentKpiAggregatorDto initializeResultAggregator() {
        return EquipmentKpiAggregatorDto.builder()
                .qualityKpi(new EquipmentKpiDto())
                .availabilityKpi(new EquipmentKpiDto())
                .performanceKpi(new EquipmentKpiDto())
                .overallEquipmentEffectivenessKpi(new EquipmentKpiDto())
                .build();
    }

    private void updateTargets(EquipmentKpiAggregatorDto result, int size) {
        result.getQualityKpi().setKpiTarget(result.getQualityKpi().getKpiTarget() / size);
        result.getAvailabilityKpi().setKpiTarget(result.getAvailabilityKpi().getKpiTarget() / size);
        result.getPerformanceKpi().setKpiTarget(result.getPerformanceKpi().getKpiTarget() / size);
        result.getOverallEquipmentEffectivenessKpi().setKpiTarget(result.getOverallEquipmentEffectivenessKpi().getKpiTarget() / size);
    }

    private void calculateOverallEquipmentEffectiveness(EquipmentKpiAggregatorDto result) {
        EquipmentKpiDto overallEquipmentEffectivenessKpi = result.getOverallEquipmentEffectivenessKpi();
        overallEquipmentEffectivenessKpi.setKpiValue(result.getQualityKpi().getKpiValue() * result.getAvailabilityKpi().getKpiValue() *
                result.getPerformanceKpi().getKpiValue());
        result.setOverallEquipmentEffectivenessKpi(overallEquipmentEffectivenessKpi);
    }

    private void sumEquipmentKpiDto(EquipmentKpiDto resultDto, EquipmentKpiDto inputDto) {
        if (resultDto == null || inputDto == null) {
            return;
        }

        resultDto.setKpiDividend(nullToZero(resultDto.getKpiDividend()) + nullToZero(inputDto.getKpiDividend()));
        resultDto.setKpiDivider(nullToZero(resultDto.getKpiDivider()) + nullToZero(inputDto.getKpiDivider()));
        resultDto.setKpiTarget(nullToZero(resultDto.getKpiTarget()) + nullToZero(inputDto.getKpiTarget()));
        resultDto.setKpiValue(resultDto.getKpiDividend() / resultDto.getKpiDivider());
    }

    private double nullToZero(Double value) {
        return value != null ? value : 0;
    }
}
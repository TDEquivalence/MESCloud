package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.converter.CounterRecordConverter;
import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.KpiDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordSummaryEntity;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.service.equipment.CountingEquipmentService;
import com.alcegory.mescloud.service.record.CounterRecordService;
import com.alcegory.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.alcegory.mescloud.model.filter.Filter.Property.END_DATE;
import static com.alcegory.mescloud.model.filter.Filter.Property.START_DATE;

@Service
@AllArgsConstructor
@Log
public class KpiManagementServiceImpl implements KpiManagementService {

    private final QualityKpiService qualityKpiService;
    private final AvailabilityKpiService availabilityKpiService;
    private final PerformanceKpiService performanceKpiService;
    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService countingEquipmentService;
    private final CounterRecordConverter counterRecordConverter;

    @Override
    public CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(FilterDto filter) {
        List<CounterRecordSummaryEntity> equipmentCounts = counterRecordService.getEquipmentOutputProductionPerDay(filter);
        List<CounterRecordDto> counterRecordDto = counterRecordConverter.toDtoList(equipmentCounts);
        return sortPerDay(filter, counterRecordDto);
    }

    @Override
    public CountingEquipmentKpiDto[] computeEquipmentKpi(FilterDto filter) {
        List<CounterRecordDto> equipmentCounts = counterRecordService.filterConclusionRecordsKpi(filter);
        return sortPerDay(filter, equipmentCounts);
    }

    private CountingEquipmentKpiDto[] sortPerDay(FilterDto filter, List<CounterRecordDto> equipmentCounts) {
        if (equipmentCounts.isEmpty()) {
            return new CountingEquipmentKpiDto[0];
        }

        Map<String, CountingEquipmentKpiDto> equipmentKpiByEquipmentAlias = new LinkedHashMap<>();

        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);

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
    public EquipmentKpiAggregatorDto computeAllEquipmentKpiAggregator(FilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {
        return computeEquipmentKpiAggregator(null, filter);
    }

    @Override
    public EquipmentKpiAggregatorDto computeEquipmentKpiAggregatorById(Long equipmentId, FilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {
        return computeEquipmentKpiAggregator(equipmentId, filter);
    }

    private EquipmentKpiAggregatorDto computeEquipmentKpiAggregator(Long equipmentId, FilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {

        CountingEquipmentDto countingEquipment = null;
        if (equipmentId != null) {
            countingEquipment = countingEquipmentService.findById(equipmentId)
                    .orElseThrow(() -> {
                        String msg = String.format("Unable to find counting equipment with id [%s]", equipmentId);
                        log.warning(msg);
                        return new NoSuchElementException(msg);
                    });
        }

        Double qualityTarget = (countingEquipment == null)
                ? countingEquipmentService.getAverageQualityTargetDividedByTotalCount()
                : countingEquipment.getQualityTarget();

        Double availabilityTarget = (countingEquipment == null)
                ? countingEquipmentService.getAverageAvailabilityTargetDividedByTotalCount()
                : countingEquipment.getAvailabilityTarget();

        Double performanceTarget = (countingEquipment == null)
                ? countingEquipmentService.getAveragePerformanceTargetDividedByTotalCount()
                : countingEquipment.getPerformanceTarget();

        Double overallEffectivePerformanceTarget = (countingEquipment == null)
                ? countingEquipmentService.getAverageOverallEquipmentEffectivenessTargetDividedByTotalCount()
                : countingEquipment.getOverallEquipmentEffectivenessTarget();

        Double theoreticalProduction = (countingEquipment == null)
                ? countingEquipmentService.getAverageTheoreticalProduction()
                : countingEquipment.getTheoreticalProduction();

        KpiDto qualityKpi = qualityKpiService.computeQuality(equipmentId, filter);
        EquipmentKpiDto quality = new EquipmentKpiDto(qualityTarget, qualityKpi);

        KpiDto availabilityKpi = availabilityKpiService.computeAvailability(equipmentId, filter);
        EquipmentKpiDto availability = new EquipmentKpiDto(availabilityTarget, availabilityKpi);

        KpiDto performanceKpi = performanceKpiService.computePerformance(qualityKpi, availabilityKpi, theoreticalProduction);
        EquipmentKpiDto performance = new EquipmentKpiDto(performanceTarget, performanceKpi);

        Double overallEffectivePerformance = computeOverallEffectivePerformance(qualityKpi, availabilityKpi, performanceKpi);
        EquipmentKpiDto overallEquipmentEffectiveness =
                new EquipmentKpiDto(overallEffectivePerformanceTarget, overallEffectivePerformance);

        return EquipmentKpiAggregatorDto.builder()
                .qualityKpi(quality)
                .availabilityKpi(availability)
                .performanceKpi(performance)
                .overallEquipmentEffectivenessKpi(overallEquipmentEffectiveness)
                .build();
    }

    @Override
    public List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDay(FilterDto filter) {
        return computeEquipmentKpiAggregators(filter, null);
    }

    @Override
    public List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDayById(Long equipmentId, FilterDto filter) {
        return computeEquipmentKpiAggregators(filter, equipmentId);
    }

    private List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregators(FilterDto filter, Long equipmentId) {
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

            EquipmentKpiAggregatorDto aggregator = (equipmentId != null)
                    ? computeEquipmentKpiAggregatorById(equipmentId, filter)
                    : computeAllEquipmentKpiAggregator(filter);

            equipmentKpiAggregators.add(aggregator);
        }

        return equipmentKpiAggregators;
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

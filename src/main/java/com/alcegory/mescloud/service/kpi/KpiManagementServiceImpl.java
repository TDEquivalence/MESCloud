package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.exception.IncompleteConfigurationException;
import com.alcegory.mescloud.model.converter.CounterRecordConverter;
import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentKpiDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiAggregatorDto;
import com.alcegory.mescloud.model.dto.equipment.EquipmentKpiDto;
import com.alcegory.mescloud.model.dto.kpi.KpiDto;
import com.alcegory.mescloud.model.dto.kpi.TargetValuesDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordDailySummaryEntity;
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
    public CountingEquipmentKpiDto[] getEquipmentOutputProductionPerDay(long sectionId, FilterDto filter) {
        List<CounterRecordDailySummaryEntity> equipmentCounts = counterRecordService.getEquipmentOutputProductionPerDay(sectionId, filter);
        List<CounterRecordDto> counterRecordDto = counterRecordConverter.toDtoList(equipmentCounts);
        return sortPerDay(filter, counterRecordDto);
    }

    @Override
    public CountingEquipmentKpiDto[] computeEquipmentKpi(long sectionId, FilterDto filter) {
        List<CounterRecordDto> equipmentCounts = counterRecordService.filterConclusionRecordsKpi(sectionId, filter);
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
    public EquipmentKpiAggregatorDto computeAllEquipmentKpiAggregator(Long sectionId, FilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {

        TargetValuesDto targetValuesDto = getTargetValues(sectionId, null);
        return computeEquipmentKpiAggregator(sectionId, null, filter, targetValuesDto);
    }

    @Override
    public EquipmentKpiAggregatorDto computeEquipmentKpiAggregatorById(Long sectionId, Long equipmentId, FilterDto filter)
            throws NoSuchElementException, IncompleteConfigurationException, ArithmeticException {

        TargetValuesDto targetValuesDto = getTargetValues(sectionId, equipmentId);
        return computeEquipmentKpiAggregator(sectionId, equipmentId, filter, targetValuesDto);
    }

    private EquipmentKpiAggregatorDto computeEquipmentKpiAggregator(Long sectionId, Long equipmentId, FilterDto filter,
                                                                    TargetValuesDto targetValues) {

        KpiDto qualityKpi = qualityKpiService.computeQuality(sectionId, equipmentId, filter);
        EquipmentKpiDto quality = new EquipmentKpiDto(targetValues.getQualityTarget(), qualityKpi);

        KpiDto availabilityKpi = availabilityKpiService.computeAvailability(sectionId, equipmentId, filter);
        EquipmentKpiDto availability = new EquipmentKpiDto(targetValues.getAvailabilityTarget(), availabilityKpi);

        KpiDto performanceKpi = performanceKpiService.computePerformance(qualityKpi, availabilityKpi, targetValues.getTheoreticalProduction());
        EquipmentKpiDto performance = new EquipmentKpiDto(targetValues.getPerformanceTarget(), performanceKpi);

        Double overallEffectivePerformance = computeOverallEffectivePerformance(qualityKpi, availabilityKpi, performanceKpi);
        EquipmentKpiDto overallEquipmentEffectiveness = new EquipmentKpiDto(targetValues.getOverallEffectivePerformanceTarget(), overallEffectivePerformance);

        return EquipmentKpiAggregatorDto.builder()
                .qualityKpi(quality)
                .availabilityKpi(availability)
                .performanceKpi(performance)
                .overallEquipmentEffectivenessKpi(overallEquipmentEffectiveness)
                .build();
    }

    private List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregators(Long sectionId, Long equipmentId, FilterDto filter) {

        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<EquipmentKpiAggregatorDto> equipmentKpiAggregators = new ArrayList<>();

        LocalDateTime startLocalDateTime = startDate.toLocalDateTime();
        LocalDateTime endLocalDateTime = endDate.toLocalDateTime().plusDays(1).minusNanos(1);

        TargetValuesDto targetValues = getTargetValues(sectionId, equipmentId);

        for (LocalDate currentDay = startLocalDateTime.toLocalDate();
             !currentDay.isAfter(endLocalDateTime.toLocalDate());
             currentDay = currentDay.plusDays(1)) {

            LocalDateTime startOfDay = currentDay.atStartOfDay();
            LocalDateTime endOfDay = currentDay.plusDays(1).atStartOfDay().minusNanos(1);

            String startDateFilter = startOfDay.format(formatter);
            String endDateTimeFilter = endOfDay.format(formatter);

            filter.getSearch().setSearchValueByName(START_DATE, startDateFilter);
            filter.getSearch().setSearchValueByName(END_DATE, endDateTimeFilter);

            EquipmentKpiAggregatorDto aggregator = computeEquipmentKpiAggregator(sectionId, equipmentId, filter, targetValues);

            equipmentKpiAggregators.add(aggregator);
        }

        return equipmentKpiAggregators;
    }

    @Override
    public List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDay(Long sectionId, FilterDto filter) {
        return computeEquipmentKpiAggregators(sectionId, null, filter);
    }

    @Override
    public List<EquipmentKpiAggregatorDto> computeEquipmentKpiAggregatorPerDayById(Long sectionId, Long equipmentId, FilterDto filter) {
        return computeEquipmentKpiAggregators(sectionId, equipmentId, filter);
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

    private TargetValuesDto getTargetValues(Long sectionId, Long equipmentId) {
        CountingEquipmentDto countingEquipment = (equipmentId != null)
                ? countingEquipmentService.findById(equipmentId).orElse(null)
                : null;

        if (countingEquipment == null) {
            return new TargetValuesDto(
                    countingEquipmentService.getAverageQualityTargetDividedByTotalCount(sectionId),
                    countingEquipmentService.getAverageAvailabilityTargetDividedByTotalCount(sectionId),
                    countingEquipmentService.getAveragePerformanceTargetDividedByTotalCount(sectionId),
                    countingEquipmentService.getAverageOverallEquipmentEffectivenessTargetDividedByTotalCount(sectionId),
                    countingEquipmentService.getAverageTheoreticalProduction(sectionId)
            );
        } else {
            return new TargetValuesDto(
                    countingEquipment.getQualityTarget(),
                    countingEquipment.getAvailabilityTarget(),
                    countingEquipment.getPerformanceTarget(),
                    countingEquipment.getOverallEquipmentEffectivenessTarget(),
                    countingEquipment.getTheoreticalProduction()
            );
        }
    }
}

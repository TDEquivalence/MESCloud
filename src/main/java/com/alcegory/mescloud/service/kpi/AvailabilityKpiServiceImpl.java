package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.KpiDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordSummaryEntity;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.service.record.CounterRecordService;
import com.alcegory.mescloud.utility.DateUtil;
import com.alcegory.mescloud.utility.DoubleUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alcegory.mescloud.model.filter.Filter.Property.END_DATE;
import static com.alcegory.mescloud.model.filter.Filter.Property.START_DATE;

@Service
@AllArgsConstructor
public class AvailabilityKpiServiceImpl implements AvailabilityKpiService {

    private static final int ACTIVE_TIME_THRESHOLD_SECONDS = 60;
    private final CounterRecordService counterRecordService;

    @Override
    public KpiDto computeAvailability(Long equipmentId, FilterDto filter) {
        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);
        
        List<CounterRecordSummaryEntity> counterRecords = counterRecordService.findByEquipmentAndPeriod(equipmentId, filter);

        long totalScheduledTime = 0L;
        long totalActiveTime = 0L;

        Map<String, List<CounterRecordSummaryEntity>> recordsByProductionOrder = counterRecords.stream()
                .collect(Collectors.groupingBy(CounterRecordSummaryEntity::getProductionOrderCode));

        for (Map.Entry<String, List<CounterRecordSummaryEntity>> entry : recordsByProductionOrder.entrySet()) {
            List<CounterRecordSummaryEntity> records = entry.getValue();

            for (CounterRecordSummaryEntity counterRecord : records) {
                totalActiveTime += counterRecord.getActiveTimeDay();
            }

            Timestamp adjustedStartDate = calculateAdjustedStartDate(records.get(0), startDate);
            Timestamp adjustedEndDate = calculateAdjustedEndDate(records.get(0), endDate);

            totalScheduledTime += DateUtil.calculateScheduledTimeInSeconds(adjustedStartDate, adjustedEndDate);
        }

        totalActiveTime -= ACTIVE_TIME_THRESHOLD_SECONDS;
        KpiDto kpi = new KpiDto(DoubleUtil.safeDoubleValue(totalActiveTime), DoubleUtil.safeDoubleValue(totalScheduledTime));
        kpi.setValueAsDivision();
        return kpi;
    }

    private Timestamp calculateAdjustedStartDate(CounterRecordSummaryEntity counterRecord, Timestamp startDate) {
        Instant createdAt = counterRecord.getCreatedAt().toInstant();
        Instant adjustedStartDate = createdAt.isAfter(startDate.toInstant()) ? createdAt : startDate.toInstant();
        return Timestamp.from(adjustedStartDate);
    }

    private Timestamp calculateAdjustedEndDate(CounterRecordSummaryEntity counterRecord, Timestamp endDate) {
        Instant completedAtInstant;

        if (counterRecord.getCompletedAt() != null) {
            completedAtInstant = DateUtil.determineCompletedAt(counterRecord.getCompletedAt(), endDate);
        } else {
            completedAtInstant = endDate.toInstant();
        }

        if (completedAtInstant.isAfter(Instant.now())) {
            Timestamp lastCounterRecordRegistered = counterRecordService.getLastRegisteredAtByProductionOrderId(counterRecord.getProductionOrderId());
            completedAtInstant = lastCounterRecordRegistered != null ? lastCounterRecordRegistered.toInstant() : Instant.now();
        }

        return Timestamp.from(completedAtInstant);
    }
}

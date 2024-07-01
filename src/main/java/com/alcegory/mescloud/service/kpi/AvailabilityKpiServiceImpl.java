package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.kpi.KpiDto;
import com.alcegory.mescloud.model.entity.records.CounterRecordDailySummaryEntity;
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
    public KpiDto computeAvailability(Long sectionId, Long equipmentId, FilterDto filter) {
        Timestamp startDate = filter.getSearch().getTimestampValue(START_DATE);
        Timestamp endDate = filter.getSearch().getTimestampValue(END_DATE);

        List<CounterRecordDailySummaryEntity> counterRecords = counterRecordService.findByEquipmentAndPeriod(sectionId,
                equipmentId, filter);

        long totalScheduledTime = 0L;
        long totalActiveTime = 0L;

        Map<String, List<CounterRecordDailySummaryEntity>> recordsByProductionOrder = counterRecords.stream()
                .collect(Collectors.groupingBy(CounterRecordDailySummaryEntity::getProductionOrderCode));

        for (Map.Entry<String, List<CounterRecordDailySummaryEntity>> entry : recordsByProductionOrder.entrySet()) {
            List<CounterRecordDailySummaryEntity> records = entry.getValue();

            if (records.isEmpty()) {
                continue;
            }

            for (CounterRecordDailySummaryEntity counterRecord : records) {
                long activeTime = counterRecord.getActiveTimeDay();
                if (activeTime > ACTIVE_TIME_THRESHOLD_SECONDS) {
                    totalActiveTime += activeTime - ACTIVE_TIME_THRESHOLD_SECONDS;
                } else {
                    totalActiveTime += activeTime;
                }
            }

            Timestamp adjustedStartDate = calculateAdjustedStartDate(records.get(0), startDate);
            Timestamp adjustedEndDate = calculateAdjustedEndDate(records.get(0), endDate);

            totalScheduledTime += DateUtil.calculateScheduledTimeInSeconds(adjustedStartDate, adjustedEndDate);
        }

        KpiDto kpi = new KpiDto(DoubleUtil.safeDoubleValue(totalActiveTime), DoubleUtil.safeDoubleValue(totalScheduledTime));
        kpi.setValueAsDivision();
        return kpi;
    }

    private Timestamp calculateAdjustedStartDate(CounterRecordDailySummaryEntity counterRecord, Timestamp startDate) {
        Instant createdAt = counterRecord.getCreatedAt().toInstant();
        Instant adjustedStartDate = createdAt.isAfter(startDate.toInstant()) ? createdAt : startDate.toInstant();
        return Timestamp.from(adjustedStartDate);
    }

    private Timestamp calculateAdjustedEndDate(CounterRecordDailySummaryEntity counterRecord, Timestamp endDate) {
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

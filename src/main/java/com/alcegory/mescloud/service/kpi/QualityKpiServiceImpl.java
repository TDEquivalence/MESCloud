package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.KpiDto;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.service.record.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QualityKpiServiceImpl implements QualityKpiService {

    private final CounterRecordService counterRecordService;

    @Override
    public KpiDto computeQuality(Long equipmentId, FilterDto filter) {
        Integer validCounter = counterRecordService.sumValidCounterIncrement(equipmentId, filter);
        Integer totalCounter = counterRecordService.sumTotalCounterIncrement(equipmentId, filter);

        KpiDto kpi = new KpiDto(validCounter, totalCounter);
        kpi.setValueAsDivision();
        return kpi;
    }
}

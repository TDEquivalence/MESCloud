package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.kpi.KpiDto;
import com.alcegory.mescloud.model.filter.FilterDto;
import com.alcegory.mescloud.service.record.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QualityKpiServiceImpl implements QualityKpiService {

    private final CounterRecordService counterRecordService;

    @Override
    public KpiDto computeQuality(Long sectionId, Long equipmentId, FilterDto filter) {
        Integer validCounter = counterRecordService.sumValidCounterIncrement(sectionId, equipmentId, filter);
        Integer totalCounter = counterRecordService.sumTotalCounterIncrement(sectionId, equipmentId, filter);

        KpiDto kpi = new KpiDto(validCounter, totalCounter);
        kpi.setValueAsDivision();
        return kpi;
    }
}

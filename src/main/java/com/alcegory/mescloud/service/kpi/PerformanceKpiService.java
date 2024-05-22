package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.KpiDto;

public interface PerformanceKpiService {

    KpiDto computePerformance(KpiDto qualityKpi, KpiDto availabilityKpi, Double theoreticalProduction);
}

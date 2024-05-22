package com.alcegory.mescloud.service.kpi;

import com.alcegory.mescloud.model.dto.KpiDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PerformanceKpiServiceImpl implements PerformanceKpiService {

    @Override
    public KpiDto computePerformance(KpiDto qualityKpi, KpiDto availabilityKpi, Double theoreticalProduction) {

        if (theoreticalProduction == null) {
            log.error("Unable to compute performance: no theoretical production configuration value");
            return null;
        }

        if (qualityKpi.getDivider() == 0 || availabilityKpi.getDividend() == 0) {
            log.error(String.format("Unable to compute performance: cannot divide quality dividend [%s] by the availability dividend [%s]",
                    qualityKpi.getDividend(), availabilityKpi.getDividend()));
            return null;
        }

        Double realProductionInSeconds = qualityKpi.getDivider() / availabilityKpi.getDividend();
        KpiDto kpi = new KpiDto(realProductionInSeconds, theoreticalProduction);
        kpi.setValueAsDivision();
        return kpi;
    }
}

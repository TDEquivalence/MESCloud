package com.alcegory.mescloud.model.dto.kpi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TargetValuesDto {

    private final Double qualityTarget;
    private final Double availabilityTarget;
    private final Double performanceTarget;
    private final Double overallEffectivePerformanceTarget;
    private final Double theoreticalProduction;
}

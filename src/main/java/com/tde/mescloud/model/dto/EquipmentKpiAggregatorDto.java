package com.tde.mescloud.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentKpiAggregatorDto {

    private EquipmentKpiDto qualityKpi;
    private EquipmentKpiDto availabilityKpi;
    private EquipmentKpiDto performanceKpi;
    private EquipmentKpiDto overallEffectiveness;
}

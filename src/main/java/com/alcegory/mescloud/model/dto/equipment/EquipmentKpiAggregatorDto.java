package com.alcegory.mescloud.model.dto.equipment;

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
    private EquipmentKpiDto overallEquipmentEffectivenessKpi;
}

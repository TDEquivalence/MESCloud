package com.alcegory.mescloud.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentKpiDto {

    private Double kpiTarget;
    private Double kpiValue;

    public EquipmentKpiDto(CountingEquipmentDto equipment, KpiDto kpi) {
        this.kpiTarget = equipment == null ? null : equipment.getQualityTarget();
        this.kpiValue = kpi == null ? null : kpi.getValue();
    }

    public EquipmentKpiDto(CountingEquipmentDto equipment, Double kpiValue) {
        this.kpiTarget = equipment == null ? null : equipment.getQualityTarget();
        this.kpiValue = kpiValue;
    }
}

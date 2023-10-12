package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentKpiDto {

    private Double kpiTarget;
    private Double kpiValue;

    public EquipmentKpiDto(Double target, KpiDto kpi) {
        this.kpiTarget = target;
        this.kpiValue = kpi == null ? null : kpi.getValue();
    }

    public EquipmentKpiDto(Double target, Double kpiValue) {
        this.kpiTarget = target;
        this.kpiValue = kpiValue;
    }
}

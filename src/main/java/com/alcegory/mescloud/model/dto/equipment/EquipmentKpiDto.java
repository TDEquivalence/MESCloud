package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.KpiDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentKpiDto {

    private Double kpiTarget;
    private Double kpiValue;
    private Double kpiDividend;
    private Double kpiDivider;

    public EquipmentKpiDto(Double target, KpiDto kpi) {
        this.kpiTarget = target;
        this.kpiValue = kpi == null ? null : kpi.getValue();
        this.kpiDividend = kpi == null ? null : kpi.getDividend();
        this.kpiDivider = kpi == null ? null : kpi.getDivider();
    }

    public EquipmentKpiDto(Double target, Double kpiValue) {
        this.kpiTarget = target;
        this.kpiValue = kpiValue;
    }
}

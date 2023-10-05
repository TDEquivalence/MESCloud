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

    private Double kpiValue;
    private Double kpiTarget;
}

package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountingEquipmentKpiDto {

    public CountingEquipmentKpiDto(final String equipmentAlias, final int numberOfTimeUnits) {
        this.equipmentAlias = equipmentAlias;
        validCounts = new int[numberOfTimeUnits];
        invalidCounts = new int[numberOfTimeUnits];
    }

    private String equipmentAlias;
    private int[] validCounts;
    private int[] invalidCounts;
}

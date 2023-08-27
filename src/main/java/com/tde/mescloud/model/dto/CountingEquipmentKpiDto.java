package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountingEquipmentKpiDto {

    private static final int ZERO_BASED_INDEX_OFFSET = 1;

    public CountingEquipmentKpiDto(final String equipmentAlias, final int numberOfTimeUnits) {
        this.equipmentAlias = equipmentAlias;
        validCounts = new int[numberOfTimeUnits - ZERO_BASED_INDEX_OFFSET];
        invalidCounts = new int[numberOfTimeUnits];
    }

    private String equipmentAlias;
    private int[] validCounts;
    private int[] invalidCounts;


    public void updateCounts(int timeUnitAsIndex, CounterRecordDto equipmentCount) {
        if (equipmentCount.isValidForProduction()) {
            validCounts[timeUnitAsIndex] += equipmentCount.getComputedValue();
        } else {
            invalidCounts[timeUnitAsIndex] += equipmentCount.getComputedValue();
        }
    }
}

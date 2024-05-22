package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountingEquipmentKpiDto {

    private String equipmentAlias;
    private int[] validCounts;
    private int[] invalidCounts;

    public CountingEquipmentKpiDto(final String equipmentAlias, final int numberOfTimeUnits) {
        this.equipmentAlias = equipmentAlias;
        validCounts = new int[numberOfTimeUnits];
        invalidCounts = new int[numberOfTimeUnits];
    }

    public void updateCounts(int timeUnitAsIndex, CounterRecordDto equipmentCount) {
        if (equipmentCount.isValidForProduction()) {
            validCounts[timeUnitAsIndex] += equipmentCount.getIncrementDay();
        } else {
            invalidCounts[timeUnitAsIndex] += equipmentCount.getIncrementDay();
        }
    }
}

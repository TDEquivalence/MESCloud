package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterRecordSearchDto {
    private String equipmentAlias;
    private String productionOrderCode;
    private String equipmentOutputAlias;
    private Integer computedValue;
    private String dateStart;
    private String dateEnd;
    private String registeredAt;
}

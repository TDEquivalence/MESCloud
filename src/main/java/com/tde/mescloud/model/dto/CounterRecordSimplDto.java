package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CounterRecordSimplDto {

    private long id;
    private String equipmentAlias;
    private int productionOrderId;
    private int equipmentOutputId;
    private int computedValue;
    private Date registeredAt;
    private boolean isValidForProduction;
}

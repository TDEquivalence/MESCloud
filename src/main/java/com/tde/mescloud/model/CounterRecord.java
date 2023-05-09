package com.tde.mescloud.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CounterRecord {

    private String productionOrderCode;
    private String equipmentCode;
    private String equipmentOutputCode;
    private String equipmentOutputAlias;
    private int equipmentStatus;
    private int realValue;
    private int computedValue;
    private Date registeredAt;
}

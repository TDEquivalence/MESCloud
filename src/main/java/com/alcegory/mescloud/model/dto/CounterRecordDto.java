package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CounterRecordDto {
    private long id;
    private String equipmentAlias;
    private String productionOrderCode;
    private int equipmentOutputId;
    private String equipmentOutputAlias;
    private int computedValue;
    private Date registeredAt;
    private boolean isValidForProduction;
    private String ims;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
}

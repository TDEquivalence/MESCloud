package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CounterRecordDto {
    
    private long id;
    private String equipmentAlias;
    private String productionOrderCode;
    private int equipmentOutputId;
    private String equipmentOutputAlias;
    private int computedValue;
    private Timestamp registeredAt;
    private boolean isValidForProduction;
    private String ims;

    private List<Map<String, Object>> instructions;
}

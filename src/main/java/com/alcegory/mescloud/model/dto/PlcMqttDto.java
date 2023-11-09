package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlcMqttDto extends AbstractMqttDto {

    private String productionOrderCode;
    private String equipmentCode;
    private int equipmentStatus;
    private long activeTime = 0L;
    private int[] alarms;
    private CounterMqttDto[] counters;
}
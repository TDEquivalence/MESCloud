package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlcMqttDto extends AbstractMqttDto {

    private String productionOrderCode;
    private String equipmentCode;
    private int equipmentStatus;
    private int activeTime;
    private int[] alarms;
    private CounterMqttDto[] counters;
}
package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlcMqttDto extends AbstractMqttDto {

    private String productionOrderCode;
    private String equipmentCode;
    private int equipmentStatus;
    private int alarmCode;
    private CounterMqttDto[] counters;
}
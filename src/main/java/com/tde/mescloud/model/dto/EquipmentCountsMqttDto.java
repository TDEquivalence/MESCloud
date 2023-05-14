package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCountsMqttDto extends AbstractMqttDto {

    private String productionOrderCode;
    private String equipmentCode;
    private int equipmentStatus;
    private CounterMqttDto[] counters;
}

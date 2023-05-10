package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCountsMqttDTO extends AbstractMqttDTO {

    private String productionOrderCode;
    private String equipmentCode;
    private int equipmentStatus;
    private CounterMqttDTO[] counters;
}

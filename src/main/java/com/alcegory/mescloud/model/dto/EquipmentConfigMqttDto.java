package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentConfigMqttDto extends AbstractMqttDto {

    private String equipmentCode;
    private int pTimerCommunicationCycle;
    private int totalOuput;
    private String[] outputCodes;
}

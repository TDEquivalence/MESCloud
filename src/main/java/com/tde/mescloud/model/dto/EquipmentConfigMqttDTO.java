package com.tde.mescloud.model.dto;

import com.tde.mescloud.constant.MqttDTOConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentConfigMqttDTO extends AbstractMqttDTO {

    private String equipmentCode;
    private int pTimerCommunicationCycle;
    private int totalOuput;
    private String[] outputCodes;
}

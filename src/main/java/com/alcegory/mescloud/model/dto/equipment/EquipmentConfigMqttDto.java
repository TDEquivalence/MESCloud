package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.mqqt.AbstractMqttDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"jsonType", "equipmentCode", "pTimerCommunicationCycle", "totalOuput", "outputCodes"})
public class EquipmentConfigMqttDto extends AbstractMqttDto {

    private String equipmentCode;
    @JsonProperty("pTimerCommunicationCycle")
    private int pTimerCommunicationCycle;
    private int totalOuput;
    private String[] outputCodes;
}


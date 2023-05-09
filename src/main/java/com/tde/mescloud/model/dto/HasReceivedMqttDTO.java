package com.tde.mescloud.model.dto;

import com.tde.mescloud.constant.MqttDTOConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HasReceivedMqttDTO extends AbstractMqttDTO {

    private String equipmentCode;
    private boolean hasReceived;

    public HasReceivedMqttDTO(String equipmentCode, boolean hasReceived) {
        this.setJsonType(MqttDTOConstants.HAS_RECEIVED_DTO_NAME);
        this.equipmentCode = equipmentCode;
        this.hasReceived = hasReceived;
    }

    public HasReceivedMqttDTO(boolean hasReceived) {
        this.setJsonType(MqttDTOConstants.HAS_RECEIVED_DTO_NAME);
        this.equipmentCode = null;
        this.hasReceived = hasReceived;
    }
}

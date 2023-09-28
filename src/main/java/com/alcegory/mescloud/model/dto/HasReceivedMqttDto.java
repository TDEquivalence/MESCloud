package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HasReceivedMqttDto extends AbstractMqttDto {

    private String equipmentCode;
    private final boolean hasReceived = true;

    public HasReceivedMqttDto(String equipmentCode) {
        this.setJsonType(MqttDTOConstants.HAS_RECEIVED_DTO_NAME);
        this.equipmentCode = equipmentCode;
    }
}

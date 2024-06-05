package com.alcegory.mescloud.model.dto.mqqt;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HasReceivedMqttDto extends AbstractMqttDto {

    private static final boolean HAS_RECEIVED = true;
    private String equipmentCode;

    public HasReceivedMqttDto() {
    }

    public HasReceivedMqttDto(String equipmentCode) {
        this.setJsonType(MqttDTOConstants.HAS_RECEIVED_DTO_NAME);
        this.equipmentCode = equipmentCode;
    }
}

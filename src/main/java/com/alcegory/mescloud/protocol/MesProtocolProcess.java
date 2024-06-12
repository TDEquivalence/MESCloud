package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.dto.mqqt.MqttDto;
import com.amazonaws.services.iot.client.AWSIotMessage;

public interface MesProtocolProcess<T extends MqttDto> {

    void execute(T mqttDTO, AWSIotMessage message);
}

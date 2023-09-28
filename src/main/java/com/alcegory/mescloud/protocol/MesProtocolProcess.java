package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.dto.MqttDto;

public interface MesProtocolProcess<T extends MqttDto> {

    void execute(T mqttDTO);
}

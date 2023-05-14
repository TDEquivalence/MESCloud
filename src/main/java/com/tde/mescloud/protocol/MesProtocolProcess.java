package com.tde.mescloud.protocol;

import com.tde.mescloud.model.dto.MqttDto;

public interface MesProtocolProcess<T extends MqttDto> {

    void execute(T mqttDTO);
}

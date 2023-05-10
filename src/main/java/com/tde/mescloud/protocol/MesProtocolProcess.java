package com.tde.mescloud.protocol;

import com.tde.mescloud.model.dto.MqttDTO;

public interface MesProtocolProcess<T extends MqttDTO> {

    void execute(T mqttDTO);
}

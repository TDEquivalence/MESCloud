package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.dto.mqqt.MqttDto;
<<<<<<< HEAD
=======
import com.amazonaws.services.iot.client.AWSIotMessage;
>>>>>>> test_environment

public interface MesProtocolProcess<T extends MqttDto> {

    void execute(T mqttDTO, AWSIotMessage message);
}

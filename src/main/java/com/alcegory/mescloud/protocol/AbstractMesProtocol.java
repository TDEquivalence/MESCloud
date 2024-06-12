package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.dto.mqqt.MqttDto;
import com.amazonaws.services.iot.client.AWSIotMessage;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

@Log
public abstract class AbstractMesProtocol implements MesProtocol {
    private static final Map<String, MesProtocolProcess<? extends MqttDto>> mesProcessByDTOName = new HashMap<>();

    public static <T extends MqttDto> void registerMesProcess(String mesDTOName, MesProtocolProcess<T> mesProtocolProcess) {
        mesProcessByDTOName.put(mesDTOName, mesProtocolProcess);
    }

    public void executeMesProcess(MqttDto mqttDTO, AWSIotMessage message) {
        @SuppressWarnings("unchecked")
        MesProtocolProcess<MqttDto> mesProtocolProcess = (MesProtocolProcess<MqttDto>) mesProcessByDTOName.get(mqttDTO.getJsonType());
        if (mesProtocolProcess == null) {
            log.warning(() -> String.format("Unable to find a MES Protocol Process for the JSON Type [%s]", mqttDTO.getJsonType()));
        } else {
            mesProtocolProcess.execute(mqttDTO, message);
        }
    }
}

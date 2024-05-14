package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.dto.mqqt.MqttDto;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

@Log
public abstract class AbstractMesProtocol implements MesProtocol {

    private static final Map<String, MesProtocolProcess> mesProcessByDTOName = new HashMap<>();


    public static void registerMesProcess(String mesDTOName, MesProtocolProcess mesProtocolProcess) {
        mesProcessByDTOName.put(mesDTOName, mesProtocolProcess);
    }

    public void executeMesProcess(MqttDto mqttDTO) {
        MesProtocolProcess mesProtocolProcess = mesProcessByDTOName.get(mqttDTO.getJsonType());
        if (mesProtocolProcess == null) {
            log.warning(() -> String.format("Unable to find a MES Protocol Process for the JSON Type [%s]", mqttDTO.getJsonType()));
        } else {
            mesProtocolProcess.execute(mqttDTO);
        }
    }
}

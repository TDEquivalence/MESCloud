package com.tde.mescloud.protocol;

import com.tde.mescloud.model.dto.MqttDto;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

@Log
public abstract class AbstractMesProtocol implements MesProtocol {

    private static final Map<String, MesProtocolProcess> mesProcessByDTOName = new HashMap<>();


    public static void registerMesProcess(String mesDTOName, MesProtocolProcess mesProtocolProcess) {
        mesProcessByDTOName.put(mesDTOName, mesProtocolProcess);
    }

    //TODO: Consider updating the equipment status at this level, since all mqttProcesses do it. This would imply changing this method argument from MqttDto to PlcMqttDto
    public void executeMesProcess(MqttDto mqttDTO) {
        MesProtocolProcess mesProtocolProcess = mesProcessByDTOName.get(mqttDTO.getJsonType());
        if (mesProtocolProcess == null) {
            log.warning(() -> String.format("Unable to find a MES Protocol Process for the JSON Type [%s]", mqttDTO.getJsonType()));
        } else {
            mesProtocolProcess.execute(mqttDTO);
        }
    }
}

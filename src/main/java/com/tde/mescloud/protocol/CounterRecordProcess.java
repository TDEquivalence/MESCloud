package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.model.dto.MqttDTO;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class CounterRecordProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDTO> {
    @Override
    public void execute(EquipmentCountsMqttDTO mqttDTO) {
        //Persist Counter Record
        //Check for alert needs
           //Counter values are the same for 3 consecutive counts
           //Equipment status is not 1
           //3 pTimerCommunicationCycles without receiving counts
        System.out.println("Counting Record Operation");
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.COUNTING_RECORD_DTO_NAME;
    }
}

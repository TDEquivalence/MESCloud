package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.service.CounterRecordService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class CounterRecordProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDTO> {

    private final CounterRecordService counterRecordService;

    public CounterRecordProcess(CounterRecordService counterRecordService) {
        this.counterRecordService = counterRecordService;
    }

    @Override
    public void execute(EquipmentCountsMqttDTO equipmentCountsMqttDTO) {
        //TODO: Check if it is a valid counter record process. Is there any CounterRecord for this PO? If not, problem!
        log.info("Executing Counter Record process");
        counterRecordService.save(equipmentCountsMqttDTO);
        //TODO: Check for alert needs
           //Counter values are the same for 3 consecutive counts
           //Equipment status is not 1
           //3 pTimerCommunicationCycles without receiving counts
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.COUNTING_RECORD_DTO_NAME;
    }
}

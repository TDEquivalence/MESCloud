package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.service.CounterRecordService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class CounterRecordProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDto> {

    private final CounterRecordService counterRecordService;

    public CounterRecordProcess(CounterRecordService counterRecordService) {
        this.counterRecordService = counterRecordService;
    }

    @Override
    public void execute(EquipmentCountsMqttDto equipmentCountsMqttDTO) {
        log.info("Executing Counter Record process");
        if (areInvalidContinuationCounts(equipmentCountsMqttDTO)) {
            log.warning(() -> String.format("Invalid continuation Counter Record - Production Order [%s] has records no or does not exist",
                    equipmentCountsMqttDTO.getProductionOrderCode()));
            return;
        }
        counterRecordService.save(equipmentCountsMqttDTO);

        //TODO: Check for alert needs
           //Counter values are the same for 3 consecutive counts
           //Equipment status is not 1
           //3 pTimerCommunicationCycles without receiving counts
    }

    private boolean areInvalidContinuationCounts(EquipmentCountsMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidContinuationCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.COUNTING_RECORD_DTO_NAME;
    }
}

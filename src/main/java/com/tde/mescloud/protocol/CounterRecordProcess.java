package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.PlcMqttDto;
import com.tde.mescloud.service.CounterRecordService;
import com.tde.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@AllArgsConstructor
public class CounterRecordProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;


    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Counter Record process");
        equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());

        if (areInvalidContinuationCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid continuation count - Production Order [%s] has no initial records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
        }

        counterRecordService.save(equipmentCounts);

        //TODO: Check for alert needs
        //Counter values are the same for 3 consecutive counts
        //Equipment status is not 1
        //3 pTimerCommunicationCycles without receiving counts
    }

    private boolean areInvalidContinuationCounts(PlcMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidContinuationCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.COUNTING_RECORD_DTO_NAME;
    }
}

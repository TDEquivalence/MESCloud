package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.service.CounterRecordService;
import com.tde.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class CounterRecordProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDto> {

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;


    @Override
    public void execute(EquipmentCountsMqttDto equipmentCounts) {
        log.info("Executing Counter Record process");
        if (areInvalidContinuationCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid continuation count - Production Order [%s] has no initial records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
            return;
        }
        counterRecordService.save(equipmentCounts);

        Optional<CountingEquipmentDto> countingEquipmentOpt =
                equipmentService.findByCode(equipmentCounts.getEquipmentCode());
        if (countingEquipmentOpt.isEmpty()) {
            return;
            //TODO: handle exception
        }

        CountingEquipmentDto countingEquipment = countingEquipmentOpt.get();
        countingEquipment.setEquipmentStatus(equipmentCounts.getEquipmentStatus());
        equipmentService.save(countingEquipment);

        //TODO: Update equipment status

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

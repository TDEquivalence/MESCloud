package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.service.CounterRecordService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.utility.LockUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@AllArgsConstructor
public class CounterRecordProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private final String EMPTY_PRODUCTION_ORDER = "";
    private final LockUtil lockHandler;


    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;


    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Counter Record process");
        equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());

        if (isCleanCounterRecord(equipmentCounts) && isProductionOrderCompleted(equipmentCounts.getEquipmentCode())) {
            lockHandler.unlock(equipmentCounts.getEquipmentCode());
        }

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

    private boolean isCleanCounterRecord(PlcMqttDto plcCounterRecord) {
        if (plcCounterRecord != null && MqttDTOConstants.COUNTING_RECORD_DTO_NAME.equals(plcCounterRecord.getJsonType())) {
            return EMPTY_PRODUCTION_ORDER.equals(plcCounterRecord.getProductionOrderCode()) && plcCounterRecord.getEquipmentStatus() == 0;
        }
        return false;
    }

    private boolean isProductionOrderCompleted(String equipmentCode) {
        return equipmentService.isProductionOrderCompleted(equipmentCode);
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.COUNTING_RECORD_DTO_NAME;
    }
}

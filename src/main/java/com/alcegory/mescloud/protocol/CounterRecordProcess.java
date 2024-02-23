package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.service.AlarmService;
import com.alcegory.mescloud.service.CounterRecordService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@AllArgsConstructor
public class CounterRecordProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    
    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;
    private final AlarmService alarmService;


    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Counter Record process");
        equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());
        alarmService.processAlarms(equipmentCounts);

        if (areInvalidContinuationCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid continuation count - Production Order [%s] has no initial records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
        }

        setOperationStatus(equipmentCounts);
        counterRecordService.processCounterRecord(equipmentCounts);
    }

    private boolean areInvalidContinuationCounts(PlcMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidContinuationCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.COUNTING_RECORD_DTO_NAME;
    }

    private void setOperationStatus(PlcMqttDto equipmentCounts) {
        if (equipmentCounts.getProductionOrderCode().isEmpty()) {
            log.info(() -> String.format("Change status to IDLE for Equipment with code [%s]", equipmentCounts.getEquipmentCode()));
            equipmentService.setOperationStatusByCode(equipmentCounts.getEquipmentCode(), CountingEquipmentEntity.OperationStatus.IDLE);
        } else {
            equipmentService.setOperationStatusByCode(equipmentCounts.getEquipmentCode(), CountingEquipmentEntity.OperationStatus.IN_PROGRESS);
        }
    }
}

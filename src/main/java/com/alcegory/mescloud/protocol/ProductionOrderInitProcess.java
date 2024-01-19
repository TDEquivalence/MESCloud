package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.service.AlarmService;
import com.alcegory.mescloud.service.CounterRecordService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private static final int EQUIPMENT_STATUS_ON = 1;

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;
    private final ProductionOrderService productionOrderService;
    private final AlarmService alarmService;


    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        String equipmentCode = equipmentCounts.getEquipmentCode();

        log.info("Executing Production Order response process");
        equipmentService.updateEquipmentStatus(equipmentCode, equipmentCounts.getEquipmentStatus());
        alarmService.processAlarms(equipmentCounts);

        if (areInvalidInitialCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid initial count - Production Order [%s] already has records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
        }

        counterRecordService.processCounterRecord(equipmentCounts);
    }

    private boolean areInvalidInitialCounts(PlcMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidInitialCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_RESPONSE_DTO_NAME;
    }
}

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
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;


    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Production Order init process");
        equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());

        if (areInvalidInitialCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid initial count - Production Order [%s] already has records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
        }

        counterRecordService.save(equipmentCounts);
    }

    private boolean areInvalidInitialCounts(PlcMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidInitialCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_RESPONSE_DTO_NAME;
    }
}

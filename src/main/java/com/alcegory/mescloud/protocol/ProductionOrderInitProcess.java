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
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private final String EMPTY_PRODUCTION_ORDER = "";
    private final LockUtil lockHandler;

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;


    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        String equipmentCode = equipmentCounts.getEquipmentCode();

        log.info("Executing Production Order response process");
        equipmentService.updateEquipmentStatus(equipmentCode, equipmentCounts.getEquipmentStatus());

        if (!hasEquipmentAssociatedProductionOrder(equipmentCode) && isCleanProductionOrderResponse(equipmentCounts)
                && lockHandler.hasLock(equipmentCode)) {
            log.info(() -> String.format("Unlock lock for equipment with code [%s]",equipmentCode));
            lockHandler.unlock(equipmentCounts.getEquipmentCode());
        }

        if (areInvalidInitialCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid initial count - Production Order [%s] already has records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
        }

        counterRecordService.save(equipmentCounts);
    }

    private boolean isCleanProductionOrderResponse(PlcMqttDto plcProductionOrder) {
        if (plcProductionOrder != null && MqttDTOConstants.PRODUCTION_ORDER_RESPONSE_DTO_NAME.equals(plcProductionOrder.getJsonType())) {
            return EMPTY_PRODUCTION_ORDER.equals(plcProductionOrder.getProductionOrderCode()) && plcProductionOrder.getEquipmentStatus() == 0;
        }
        return false;
    }

    private boolean hasEquipmentAssociatedProductionOrder(String equipmentCode) {
        return equipmentService.hasEquipmentAssociatedProductionOrder(equipmentCode);
    }

    private boolean areInvalidInitialCounts(PlcMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidInitialCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_RESPONSE_DTO_NAME;
    }
}

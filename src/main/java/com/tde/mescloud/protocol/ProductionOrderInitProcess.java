package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.service.CounterRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDto> {

    private final CounterRecordService counterRecordService;


    @Override
    public void execute(EquipmentCountsMqttDto equipmentCountsMqttDTO) {
        log.info("Executing Production Order init process");
        if (areInvalidInitialCounts(equipmentCountsMqttDTO)) {
            log.warning(() -> String.format("Invalid initial count - Production Order [%s] already has records or does not exist",
                    equipmentCountsMqttDTO.getProductionOrderCode()));
            return;
        }

        counterRecordService.save(equipmentCountsMqttDTO);
        //TODO: equipmentStatus -> equipmentService.updateStatus();
    }

    private boolean areInvalidInitialCounts(EquipmentCountsMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidInitialCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_INIT_DTO_NAME;
    }
}

package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.service.CounterRecordService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDTO> {

    private final CounterRecordService counterRecordService;

    public ProductionOrderInitProcess(CounterRecordService counterRecordService) {
        this.counterRecordService = counterRecordService;
    }

    @Override
    public void execute(EquipmentCountsMqttDTO equipmentCountsDTO) {
        log.info("Executing Production Order init process");
        //TODO: Check if it is a valid init process. Is there any CounterRecord for this PO
        counterRecordService.save(equipmentCountsDTO);
        //TODO: equipmentStatus -> equipmentService.updateStatus();
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_INIT_DTO_NAME;
    }
}

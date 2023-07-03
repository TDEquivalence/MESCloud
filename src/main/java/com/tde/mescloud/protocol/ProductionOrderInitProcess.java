package com.tde.mescloud.protocol;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.service.CounterRecordService;
import com.tde.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderInitProcess extends AbstractMesProtocolProcess<EquipmentCountsMqttDto> {

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;


    @Override
    public void execute(EquipmentCountsMqttDto equipmentCounts) {
        log.info("Executing Production Order init process");
        if (areInvalidInitialCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid initial count - Production Order [%s] already has records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
            return;
        }

        List<CounterRecordDto> counterRecords = counterRecordService.save(equipmentCounts);
        if (counterRecords == null) {
            //TODO: Handle & log
            return;
        }

        Optional<CountingEquipmentDto> countingEquipmentOpt =
                equipmentService.findByCode(equipmentCounts.getEquipmentCode());
        if (countingEquipmentOpt.isEmpty()) {
            return;
            //TODO: handle exception
        }

        CountingEquipmentDto countingEquipment = countingEquipmentOpt.get();
        countingEquipment.setEquipmentStatus(equipmentCounts.getEquipmentStatus());
        equipmentService.save(countingEquipment);
    }

    private boolean areInvalidInitialCounts(EquipmentCountsMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidInitialCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_INIT_DTO_NAME;
    }
}

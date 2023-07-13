package com.tde.mescloud.protocol;

import com.tde.mescloud.api.mqtt.MqttClient;
import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.exception.MesMqttException;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.PlcMqttDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.ProductionOrderRepository;
import com.tde.mescloud.service.CounterRecordService;
import com.tde.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderConclusionProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;
    private final MqttClient mqttClient;
    private final ProductionOrderRepository repository;
    private final MesMqttSettings mqttSettings;

    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Production Order conclusion process");
        Optional<CountingEquipmentDto> countingEquipmentOpt = equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());

        if(countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to find an Equipment with code [%s]", equipmentCounts.getEquipmentCode()));
            return;
        }

        if (areInvalidInitialCounts(equipmentCounts)) {
            log.warning(() -> String.format("Invalid initial count - Production Order [%s] already has records or does not exist",
                    equipmentCounts.getProductionOrderCode()));
        }

        counterRecordService.save(equipmentCounts);
        executeProductionOrderConclusion(countingEquipmentOpt.get());
    }

    private boolean areInvalidInitialCounts(PlcMqttDto equipmentCountsMqttDTO) {
        return !counterRecordService.areValidInitialCounts(equipmentCountsMqttDTO.getProductionOrderCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_RESPONSE_DTO_NAME;
    }

    public void executeProductionOrderConclusion(CountingEquipmentDto countingEquipmentOpt) {

        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findByCode(countingEquipmentOpt.getProductionOrderCode());
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found for an Equipment with code [%s]", countingEquipmentOpt.getProductionOrderCode()));
            return;
        }

        if(!productionOrderEntityOpt.get().isCompleted()) {
            try {
                ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
                productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_DTO_NAME);
                productionOrderMqttDto.setEquipmentEnabled(false);
                productionOrderMqttDto.setProductionOrderCode("");
                productionOrderMqttDto.setTargetAmount(0);
                productionOrderMqttDto.setEquipmentCode(countingEquipmentOpt.getCode());
                mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
            } catch (MesMqttException e) {
                log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment [%s]", countingEquipmentOpt.getId()));
            }
        }

        ProductionOrderEntity productionOrderEntity = productionOrderEntityOpt.get();
        productionOrderEntity.setCompleted(true);
        repository.save(productionOrderEntity);
    }
}

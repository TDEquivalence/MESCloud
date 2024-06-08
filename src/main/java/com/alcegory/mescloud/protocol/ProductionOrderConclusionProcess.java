package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.mqqt.PlcMqttDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.service.alarm.AlarmService;
import com.alcegory.mescloud.service.management.CountingEquipmentManagementService;
import com.alcegory.mescloud.service.production.ProductionOrderService;
import com.alcegory.mescloud.service.record.CounterRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderConclusionProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private static final int THREAD_SLEEP_DURATION = 500;

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentManagementService countingEquipmentManagementService;
    private final ProductionOrderService productionOrderService;
    private final AlarmService alarmService;
    private final MqttClient mqttClient;
    private final MesMqttSettings mqttSettings;

    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Production Order conclusion process");
        if (equipmentCounts.getProductionOrderCode() == null && equipmentCounts.getEquipmentCode() == null) {
            log.warning(() -> String.format("Unable to find an Equipment with code [%s]", equipmentCounts.getEquipmentCode()));
            return;
        }

        countingEquipmentManagementService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());
        alarmService.processAlarms(equipmentCounts);

        counterRecordService.processCounterRecord(equipmentCounts);

        ProductionOrderEntity productionOrder = getProductionOrderByCode(equipmentCounts.getProductionOrderCode());
        if (productionOrder == null) {
            throw new IllegalStateException("Production order code: " + equipmentCounts.getProductionOrderCode() + "not found for equipment code: " + equipmentCounts.getEquipmentCode());
        }

        executeProductionOrderConclusion(productionOrder, equipmentCounts.getEquipmentCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_RESPONSE_DTO_NAME;
    }

    public void executeProductionOrderConclusion(ProductionOrderEntity productionOrder, String equipmentCode) {

        if (productionOrder == null) {
            log.warning(() -> String.format("No Production Order found for Equipment with code [%s]", equipmentCode));
            return;
        }

        if (!productionOrder.isCompleted()) {
            try {
                Thread.sleep(THREAD_SLEEP_DURATION);
                ProductionOrderMqttDto productionOrderMqttDto = createProductionOrderMqttDto(equipmentCode);
                mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment with code [%s]", equipmentCode));
            } catch (Exception e) {
                log.severe("An unexpected error occurred during MQTT publication: " + e.getMessage());
            }
        }

        completeProductionOrder(productionOrder);
        countingEquipmentManagementService.setOperationStatusByCode(equipmentCode, CountingEquipmentEntity.OperationStatus.IDLE);
    }

    private void completeProductionOrder(ProductionOrderEntity productionOrder) {
        productionOrderService.completeProductionOrder(productionOrder);
    }

    private ProductionOrderMqttDto createProductionOrderMqttDto(String equipmentCode) {
        ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
        productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_DTO_NAME);
        productionOrderMqttDto.setEquipmentEnabled(false);
        productionOrderMqttDto.setProductionOrderCode("");
        productionOrderMqttDto.setTargetAmount(0);
        productionOrderMqttDto.setEquipmentCode(equipmentCode);
        return productionOrderMqttDto;
    }

    private ProductionOrderEntity getProductionOrderByCode(String code) {
        return productionOrderService.getProductionOrderByCode(code);
    }
}

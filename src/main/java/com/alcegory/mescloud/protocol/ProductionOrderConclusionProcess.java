package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.service.AlarmService;
import com.alcegory.mescloud.service.CounterRecordService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderConclusionProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private static final Logger logger = LoggerFactory.getLogger(ProductionOrderConclusionProcess.class);

    private static final int THREAD_SLEEP_DURATION = 500;

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;
    private final ProductionOrderService productionOrderService;
    private final AlarmService alarmService;
    private final MqttClient mqttClient;
    private final ProductionOrderRepository repository;
    private final MesMqttSettings mqttSettings;

    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Production Order conclusion process");
        if (equipmentCounts.getProductionOrderCode() == null && equipmentCounts.getEquipmentCode() == null) {
            log.warning(() -> String.format("Unable to find an Equipment with code [%s]", equipmentCounts.getEquipmentCode()));
            return;
        }

        equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());
        alarmService.processAlarms(equipmentCounts);

        long updatedActiveTime = updateActiveTime(equipmentCounts);
        counterRecordService.processCounterRecord(equipmentCounts, updatedActiveTime);

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
            return; // No need to continue if no production order is found.
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
        setOperationStatus(equipmentCode);
    }

    private long updateActiveTime(PlcMqttDto equipmentCounts) {
        log.info("Active Time PO Conclusion: " + equipmentCounts.getActiveTime());
        long activeTime = equipmentCounts.getActiveTime();
        return productionOrderService.updateActiveTime(equipmentCounts.getProductionOrderCode(), activeTime);
    }

    private void completeProductionOrder(ProductionOrderEntity productionOrder) {
        log.info(() -> String.format("Set and save production order as completed, with code [%s]", productionOrder.getCode()));
        productionOrder.setCompleted(true);
        repository.save(productionOrder);
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
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findByCode(code);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found for an Equipment with code [%s]", code));
            return null;
        }

        return productionOrderEntityOpt.get();
    }

    private void setOperationStatus(String equipmentCode) {
        log.info(() -> String.format("Change status to IDLE for Equipment with code [%s]", equipmentCode));
        equipmentService.setOperationStatusByCode(equipmentCode, CountingEquipmentEntity.OperationStatus.IDLE);
    }
}

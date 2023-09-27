package com.tde.mescloud.protocol;

import com.tde.mescloud.api.mqtt.MqttClient;
import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.exception.MesMqttException;
import com.tde.mescloud.model.dto.PlcMqttDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.ProductionOrderRepository;
import com.tde.mescloud.service.CounterRecordService;
import com.tde.mescloud.service.CountingEquipmentService;
import com.tde.mescloud.utility.LockUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderConclusionProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private static final int THREAD_SLEEP_DURATION = 500;

    private final CounterRecordService counterRecordService;
    private final CountingEquipmentService equipmentService;
    private final MqttClient mqttClient;
    private final ProductionOrderRepository repository;
    private final MesMqttSettings mqttSettings;
    private final LockUtil lock;

    @Override
    public void execute(PlcMqttDto equipmentCounts) {

        log.info("Executing Production Order conclusion process");
        if (equipmentCounts.getProductionOrderCode() == null && equipmentCounts.getEquipmentCode() == null) {
            log.warning(() -> String.format("Unable to find an Equipment with code [%s]", equipmentCounts.getEquipmentCode()));
            return;
        }

        equipmentService.updateEquipmentStatus(equipmentCounts.getEquipmentCode(), equipmentCounts.getEquipmentStatus());
        counterRecordService.save(equipmentCounts);

        ProductionOrderEntity productionOrder = getProductionOrderByCode(equipmentCounts.getEquipmentCode());
        if (productionOrder == null) {
            throw new IllegalStateException("Production order not found for equipment code: " + equipmentCounts.getEquipmentCode());
        }

        executeProductionOrderConclusion(productionOrder, equipmentCounts.getEquipmentCode());
    }

    @Override
    public String getMatchingDTOName() {
        return MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_RESPONSE_DTO_NAME;
    }

    public void executeProductionOrderConclusion(ProductionOrderEntity productionOrder, String equipmentCode) {

        if (!productionOrder.isCompleted()) {
            try {
                Thread.sleep(THREAD_SLEEP_DURATION);
                ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
                productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_DTO_NAME);
                productionOrderMqttDto.setEquipmentEnabled(false);
                productionOrderMqttDto.setProductionOrderCode("");
                productionOrderMqttDto.setTargetAmount(0);
                productionOrderMqttDto.setEquipmentCode(equipmentCode);
                mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
            } catch (MesMqttException | InterruptedException e) {
                log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment with code [%s]", equipmentCode));
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        try {
            productionOrder.setCompleted(true);
            productionOrder.setCompletedAt(new Date());
            repository.save(productionOrder);
        } finally {
            lock.signalExecute(); // Release the lock in a finally block to ensure it's always released
        }
    }

    private ProductionOrderEntity getProductionOrderByCode(String code) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findByCode(code);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found for an Equipment with code [%s]", code));
            return null;
        }

        return productionOrderEntityOpt.get();
    }
}

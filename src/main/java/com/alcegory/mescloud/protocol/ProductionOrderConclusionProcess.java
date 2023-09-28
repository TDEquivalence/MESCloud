package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.service.CounterRecordService;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.utility.LockUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderConclusionProcess extends AbstractMesProtocolProcess<PlcMqttDto> {

    private static final Logger logger = LoggerFactory.getLogger(ProductionOrderConclusionProcess.class);

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

        try {
            productionOrder.setCompleted(true);
            productionOrder.setCompletedAt(new Date());
            repository.save(productionOrder);
        } catch (DataAccessException e) {
            log.severe("DataAccessException caught while saving production order " + e.getMessage());
        } finally {
            lock.signalExecute();
        }
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
}

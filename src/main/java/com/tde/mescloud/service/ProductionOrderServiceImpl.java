package com.tde.mescloud.service;

import com.tde.mescloud.api.mqtt.MqttClient;
import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.exception.MesMqttException;
import com.tde.mescloud.model.converter.ProductionOrderConverter;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.protocol.MesMqttSettings;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import com.tde.mescloud.repository.ProductionOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private static final String CODE_PREFIX = "PO";
    private static final String NEW_CODE_FORMAT = "%02d";
    private static final int CODE_VALUE_INDEX = 4;

    private final ProductionOrderRepository repository;
    private final ProductionOrderConverter converter;
    private final CountingEquipmentRepository countingEquipmentRepository;
    private final MqttClient mqttClient;
    private final MesMqttSettings mqttSettings;


    @Override
    public Optional<ProductionOrderDto> findByCode(String code) {
        Optional<ProductionOrderEntity> persistedProductionOrderOpt = repository.findByCode(code);
        if (persistedProductionOrderOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductionOrderDto productionOrder = converter.toDto(persistedProductionOrderOpt.get());
        return Optional.of(productionOrder);
    }

    @Override
    public boolean hasActiveProductionOrder(long countingEquipmentId) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(countingEquipmentId);
        return productionOrderEntityOpt.isPresent();
    }

    @Override
    public Optional<ProductionOrderDto> complete(long equipmentId) {

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentRepository.findById(equipmentId);
        if(countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to find an Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(equipmentId);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No active Production Order found for an Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        ProductionOrderEntity productionOrderEntity = productionOrderEntityOpt.get();
        productionOrderEntity.setCompleted(true);
        ProductionOrderEntity persistedProductionOrder = repository.save(productionOrderEntity);

        ProductionOrderDto productionOrder = converter.toDto(persistedProductionOrder);

        try {
            ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
            productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_INIT_DTO_NAME);
            productionOrderMqttDto.setEquipmentEnabled(false);
            productionOrderMqttDto.setProductionOrderCode("");
            productionOrderMqttDto.setTargetAmount(0);
            productionOrderMqttDto.setEquipmentCode(countingEquipmentOpt.get().getCode());
            mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
        } catch (MesMqttException e) {
            log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment [%s]", equipmentId));
        }

        return Optional.of(productionOrder);
    }

    @Override
    public Optional<ProductionOrderDto> save(ProductionOrderDto productionOrder) {

        Optional<CountingEquipmentEntity> countingEquipmentEntityOpt =
                countingEquipmentRepository.findById(productionOrder.getEquipmentId());
        if (countingEquipmentEntityOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to find Equipment with id [%s]", productionOrder.getEquipmentId()));
            return Optional.empty();
        }

        ProductionOrderEntity productionOrderEntity = converter.toEntity(productionOrder);
        productionOrderEntity.setCreatedAt(new Date());
        productionOrderEntity.setCompleted(false);
        productionOrderEntity.setCode(generateCode());

        CountingEquipmentEntity countingEquipmentEntity = countingEquipmentEntityOpt.get();
        countingEquipmentEntity.setId(productionOrder.getEquipmentId());
        productionOrderEntity.setEquipment(countingEquipmentEntity);

        ProductionOrderEntity persistedProductionOrder = repository.save(productionOrderEntity);

        try {
            //TODO: remove to MesProtocolProcess level
            publishToPlc(persistedProductionOrder);
        } catch (MesMqttException e) {
            log.severe("Unable to publish Production Order over MQTT.");
            return Optional.empty();
        }

        ProductionOrderDto persistedProductionOrderDto = converter.toDto(persistedProductionOrder);
        return Optional.of(persistedProductionOrderDto);
    }

    @Override
    public String generateCode() {
        return CODE_PREFIX + getYearForCode() + getNewCodeValueFormatted();
    }

    private int getYearForCode() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;
    }

    private int calculateNewCodeValue() {
        //TODO: Refactor
        ProductionOrderEntity productionOrderEntity = repository.findTopByOrderByIdDesc();
        if (productionOrderEntity == null) {
            return 1;
        }
        String lastValueAsString = productionOrderEntity.getCode().substring(CODE_VALUE_INDEX);
        int lastCodeValue = Integer.parseInt(lastValueAsString);
        return ++lastCodeValue;
    }

    private String getNewCodeValueFormatted() {
        int newCode = calculateNewCodeValue();
        return String.format(NEW_CODE_FORMAT, newCode);
    }

    private void publishToPlc(ProductionOrderEntity productionOrderEntity) throws MesMqttException {
        ProductionOrderMqttDto productionOrderMqttDto = converter.toMqttDto(productionOrderEntity, true);
        mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
    }
}

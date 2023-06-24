package com.tde.mescloud.service;

import com.tde.mescloud.api.mqtt.MqttClient;
import com.tde.mescloud.exception.MesMqttException;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.converter.ProductionOrderConverter;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.repository.CountingEquipmentRepository;
import com.tde.mescloud.repository.ProductionOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final int EQUIPMENT_PAUSED_STATUS = 0;
    private static final int EQUIPMENT_ACTIVE_STATUS = 1;

    private final ProductionOrderRepository repository;
    private final ProductionOrderConverter converter;
    private final CountingEquipmentRepository countingEquipmentRepository;
    private final MqttClient mqttClient;


    @Override
    public ProductionOrder findByCode(String code) {
        //TODO: use projection
        ProductionOrderEntity entity = repository.findByCode(code);
        return converter.convertToDomainObject(entity);
    }

    @Override
    public boolean hasActiveProductionOrder(long countingEquipmentId) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(countingEquipmentId);
        return productionOrderEntityOpt.isPresent();
    }

    @Override
    public Optional<ProductionOrder> complete(long equipmentId) {

        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(equipmentId);
        if (productionOrderEntityOpt.isEmpty()) {
            //TODO: Handle & log error
            return Optional.empty();
        }

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentRepository.findById(equipmentId);
        if(countingEquipmentOpt.isEmpty()) {
            //TODO: Handle & log error
            return Optional.empty();
        }

        ProductionOrderEntity productionOrderEntity = productionOrderEntityOpt.get();
        productionOrderEntity.setCompleted(true);
        ProductionOrderEntity persistedProductionOrder = repository.save(productionOrderEntity);

        CountingEquipmentEntity countingEquipmentEntity = countingEquipmentOpt.get();
        countingEquipmentEntity.setEquipmentStatus(EQUIPMENT_PAUSED_STATUS);
        countingEquipmentRepository.save(countingEquipmentEntity);

        ProductionOrder productionOrder = converter.convertToDomainObject(persistedProductionOrder);

        return Optional.of(productionOrder);
    }

    @Override
    @Transactional
    public ProductionOrder save(ProductionOrderDto productionOrderDto) {

        Optional<CountingEquipmentEntity> countingEquipmentEntityOpt =
                countingEquipmentRepository.findById(productionOrderDto.getEquipmentId());
        if (countingEquipmentEntityOpt.isEmpty()) {
            //TODO: Handle & log error
            return null;
        }

        ProductionOrderEntity productionOrderEntity = converter.convertToEntity(productionOrderDto);
        productionOrderEntity.setCreatedAt(new Date());
        productionOrderEntity.setCompleted(false);
        productionOrderEntity.setCode(generateCode());
        ProductionOrderEntity persistedProductionOrder = repository.save(productionOrderEntity);

        countingEquipmentEntityOpt.get().setEquipmentStatus(EQUIPMENT_ACTIVE_STATUS);

        try {
            publishToPlc(persistedProductionOrder);
        } catch (MesMqttException e) {
            log.severe("Unable to publish Production Order over MQTT.");
            return null;
        }

        return converter.convertToDomainObject(persistedProductionOrder);
    }

    @Override
    public String generateCode() {
        return CODE_PREFIX + getYearForCode() + getNewCodeValueFormatted();
    }

    private int getYearForCode() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;
    }

    private int calculateNewCodeValue() {
        ProductionOrderEntity productionOrderEntity = repository.findTopByOrderByIdDesc();
        String lastValueAsString = productionOrderEntity.getCode().substring(CODE_VALUE_INDEX);
        int lastCodeValue = Integer.parseInt(lastValueAsString);
        return ++lastCodeValue;
    }

    private String getNewCodeValueFormatted() {
        int newCode = calculateNewCodeValue();
        return String.format(NEW_CODE_FORMAT, newCode);
    }

    private void publishToPlc(ProductionOrderEntity productionOrderEntity) throws MesMqttException {
        ProductionOrderMqttDto productionOrderMqttDto = converter.convertToMqttDto(productionOrderEntity);
        mqttClient.publish("DEV/MASILVA/OBO/PROTOCOL_COUNT_V0/PLC", productionOrderMqttDto);
    }
}

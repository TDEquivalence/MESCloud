package com.tde.mescloud.model.converter;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class ProductionOrderConverterImpl implements ProductionOrderConverter {


    @Override
    public ProductionOrder convertToDomainObject(ProductionOrderEntity entity) {

        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setId(entity.getId());
        productionOrder.setCode(entity.getCode());
        productionOrder.setCreatedAt(entity.getCreatedAt());
        productionOrder.setTargetAmount(entity.getTargetAmount());
        productionOrder.setInputBatch(entity.getInputBatch());
        productionOrder.setSource(entity.getSource());
        productionOrder.setGauge(entity.getGauge());
        productionOrder.setCategory(entity.getCategory());
        productionOrder.setWashingProcess(entity.getWashingProcess());

        if (entity.getEquipment() != null) {
            productionOrder.setCountingEquipmentId(entity.getEquipment().getId());
        }

        return productionOrder;
    }

    @Override
    public ProductionOrderDto convertToDto(ProductionOrder productionOrder) {

        ProductionOrderDto productionOrderDto = new ProductionOrderDto();
        productionOrderDto.setId(productionOrder.getId());
        productionOrderDto.setCode(productionOrder.getCode());
        productionOrderDto.setTargetAmount(productionOrder.getTargetAmount());
        productionOrderDto.setCreatedAt(productionOrder.getCreatedAt());
        productionOrderDto.setInputBatch(productionOrder.getInputBatch());
        productionOrderDto.setSource(productionOrder.getSource());
        productionOrderDto.setGauge(productionOrder.getGauge());
        productionOrderDto.setCategory(productionOrder.getCategory());
        productionOrderDto.setWashingProcess(productionOrder.getWashingProcess());

        productionOrderDto.setEquipmentId(productionOrderDto.getEquipmentId());

        return productionOrderDto;
    }

    @Override
    public ProductionOrderEntity convertToEntity(ProductionOrderDto productionOrderDto) {

        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setId(productionOrderDto.getId());
        productionOrderEntity.setCode(productionOrderDto.getCode());
        productionOrderEntity.setTargetAmount(productionOrderDto.getTargetAmount());
        productionOrderEntity.setCreatedAt(productionOrderDto.getCreatedAt());
        productionOrderEntity.setInputBatch(productionOrderDto.getInputBatch());
        productionOrderEntity.setSource(productionOrderDto.getSource());
        productionOrderEntity.setGauge(productionOrderDto.getGauge());
        productionOrderEntity.setCategory(productionOrderDto.getCategory());
        productionOrderEntity.setWashingProcess(productionOrderDto.getWashingProcess());

        CountingEquipmentEntity countingEquipmentEntity = new CountingEquipmentEntity();
        countingEquipmentEntity.setId(productionOrderDto.getId());
        productionOrderEntity.setEquipment(countingEquipmentEntity);

        return productionOrderEntity;
    }

    @Override
    public ProductionOrderMqttDto convertToMqttDto(ProductionOrderEntity entity) {

        ProductionOrderMqttDto mqttDto = new ProductionOrderMqttDto();
        mqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_DTO_NAME);
        mqttDto.setProductionOrderCode(entity.getCode());
        mqttDto.setTargetAmount(entity.getTargetAmount());
        mqttDto.setEquipmentEnabled(entity.isEquipmentEnabled());

        if (entity.getEquipment() != null) {
            mqttDto.setEquipmentCode(entity.getEquipment().getCode());
        }

        return mqttDto;
    }
}

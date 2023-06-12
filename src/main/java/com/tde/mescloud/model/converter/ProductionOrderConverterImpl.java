package com.tde.mescloud.model.converter;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log
public class ProductionOrderConverterImpl implements ProductionOrderConverter {

    private CountingEquipmentConverter countingEquipmentConverter;


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
            CountingEquipment countingEquipment = countingEquipmentConverter.convertToDomainObject(entity.getEquipment());
            productionOrder.setEquipment(countingEquipment);
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

        CountingEquipmentDto countingEquipmentDto = countingEquipmentConverter.convertToDto(productionOrder.getEquipment());
        productionOrderDto.setEquipment(countingEquipmentDto);

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

        CountingEquipmentEntity countingEquipmentEntity =
                countingEquipmentConverter.convertToEntity(productionOrderDto.getEquipment());
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

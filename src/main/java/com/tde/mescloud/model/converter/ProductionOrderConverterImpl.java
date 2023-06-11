package com.tde.mescloud.model.converter;

import com.tde.mescloud.constant.MqttDTOConstants;
import com.tde.mescloud.model.ProductionOrder;
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
        return productionOrder;
    }

    @Override
    public ProductionOrderDto convertToDto(ProductionOrder productionOrder) {
        ProductionOrderDto productionOrderDto = new ProductionOrderDto();
        productionOrderDto.setId(productionOrder.getId());
        productionOrderDto.setCode(productionOrder.getCode());
        productionOrderDto.setTargetAmount(productionOrder.getTargetAmount());
        productionOrderDto.setCreatedAt(productionOrder.getCreatedAt());

        //TODO: Implement
//        CountingEquipment countingEquipment = countingEquipmentConverter...
//        productionOrderDto.setEquipment();

        return productionOrderDto;
    }

    @Override
    public ProductionOrderEntity convertToEntity(ProductionOrderDto productionOrderDto) {
        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setId(productionOrderDto.getId());
        productionOrderEntity.setCode(productionOrderDto.getCode());
        productionOrderEntity.setTargetAmount(productionOrderDto.getTargetAmount());
        productionOrderEntity.setCreatedAt(productionOrderDto.getCreatedAt());

        CountingEquipmentEntity countingEquipmentEntity =
                countingEquipmentConverter.convertToEntity(productionOrderDto.getEquipment());
        //TODO: Implement
//        productionOrderEntity.setEquipment();

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

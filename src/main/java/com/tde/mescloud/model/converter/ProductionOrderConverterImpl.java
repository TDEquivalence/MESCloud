package com.tde.mescloud.model.converter;

import com.tde.mescloud.constant.MqttDTOConstants;
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
    public ProductionOrderEntity toEntity(ProductionOrderDto productionOrderDto) {

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
    public ProductionOrderMqttDto toMqttDto(ProductionOrderEntity entity) {

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

    @Override
    public ProductionOrderDto toDto(ProductionOrderEntity entity) {
        ProductionOrderDto dto = new ProductionOrderDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setTargetAmount(entity.getTargetAmount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setGauge(entity.getGauge());
        dto.setSource(entity.getSource());
        dto.setCategory(entity.getCategory());
        dto.setWashingProcess(entity.getWashingProcess());
        dto.setInputBatch(entity.getInputBatch());

        if (entity.getEquipment() != null) {
            dto.setEquipmentId(entity.getEquipment().getId());
        }

        return dto;
    }
}

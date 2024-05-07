package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        productionOrderEntity.setCompletedAt(productionOrderDto.getCompletedAt());
        productionOrderEntity.setInputBatch(productionOrderDto.getInputBatch());
        productionOrderEntity.setSource(productionOrderDto.getSource());
        productionOrderEntity.setGauge(productionOrderDto.getGauge());
        productionOrderEntity.setCategory(productionOrderDto.getCategory());
        productionOrderEntity.setWashingProcess(productionOrderDto.getWashingProcess());

        return productionOrderEntity;
    }

    @Override
    public ProductionOrderMqttDto toMqttDto(ProductionOrderEntity entity, boolean isEquipmentEnabled) {

        ProductionOrderMqttDto mqttDto = new ProductionOrderMqttDto();
        mqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_DTO_NAME);
        mqttDto.setProductionOrderCode(entity.getCode());
        mqttDto.setTargetAmount(entity.getTargetAmount());
        mqttDto.setEquipmentEnabled(isEquipmentEnabled);

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
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setGauge(entity.getGauge());
        dto.setSource(entity.getSource());
        dto.setCategory(entity.getCategory());
        dto.setWashingProcess(entity.getWashingProcess());
        dto.setInputBatch(entity.getInputBatch());

        if (entity.getEquipment() != null) {
            dto.setEquipmentId(entity.getEquipment().getId());
        }

        dto.setProductionInstructions(entity.getProductionInstructionsMap());

        return dto;
    }

    public List<ProductionInstructionEntity> fromDtoMap(Map<String, String> instructionMap, ProductionOrderEntity productionOrder) {
        if (instructionMap == null || instructionMap.isEmpty()) {
            return Collections.emptyList();
        }

        return instructionMap.entrySet().stream()
                .map(entry -> {
                    ProductionInstructionEntity instruction = new ProductionInstructionEntity();
                    instruction.setName(entry.getKey());
                    instruction.setValue(entry.getValue());
                    instruction.setProductionOrder(productionOrder);
                    return instruction;
                })
                .toList();
    }
}

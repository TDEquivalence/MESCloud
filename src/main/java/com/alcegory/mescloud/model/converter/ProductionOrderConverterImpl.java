package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.model.dto.ProductionInstructionDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        productionOrderEntity.setProductionInstructions(toEntityList(productionOrderDto.getInstructions()));

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

        if (entity.getEquipment() != null) {
            dto.setEquipmentId(entity.getEquipment().getId());
        }

        dto.setInstructions(toDtoList(entity.getProductionInstructions()));

        return dto;
    }

    public List<ProductionInstructionDto> toDtoList(List<ProductionInstructionEntity> entities) {
        List<ProductionInstructionDto> dtos = new ArrayList<>();
        if (entities != null) {
            for (ProductionInstructionEntity entity : entities) {
                ProductionInstructionDto dto = toDto(entity);
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public ProductionInstructionDto toDto(ProductionInstructionEntity entity) {
        ProductionInstructionDto dto = new ProductionInstructionDto();
        if (entity != null) {
            dto.setName(entity.getName());
            dto.setValue(entity.getValue());
        }
        return dto;
    }

    public List<ProductionInstructionEntity> toEntityList(List<ProductionInstructionDto> dtos) {
        List<ProductionInstructionEntity> entities = new ArrayList<>();
        if (dtos != null) {
            for (ProductionInstructionDto dto : dtos) {
                ProductionInstructionEntity entity = toEntity(dto);
                entities.add(entity);
            }
        }
        return entities;
    }

    public ProductionInstructionEntity toEntity(ProductionInstructionDto dto) {
        ProductionInstructionEntity entity = new ProductionInstructionEntity();
        if (dto != null) {
            entity.setName(dto.getName());
            entity.setValue(dto.getValue());
        }
        return entity;
    }
}

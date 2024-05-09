package com.alcegory.mescloud.model.converter;


import com.alcegory.mescloud.model.dto.ProductionInstructionDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;

import java.util.List;

public interface ProductionOrderConverter {

    ProductionOrderEntity toEntity(ProductionOrderDto productionOrderDto);

    ProductionOrderEntity toEntity(RequestProductionOrderDto productionOrderDto);

    ProductionOrderMqttDto toMqttDto(ProductionOrderEntity productionOrderDto, boolean isEquipmentEnabled);

    ProductionOrderDto toDto(ProductionOrderEntity entity);

    List<ProductionInstructionDto> toDtoList(List<ProductionInstructionEntity> entities);

    default List<ProductionOrderDto> toDto(List<ProductionOrderEntity> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .toList();
    }
}

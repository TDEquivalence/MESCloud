package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.ProductionOrderEntity;

import java.util.List;
import java.util.stream.Collectors;

public interface ProductionOrderConverter {

    ProductionOrderEntity toEntity(ProductionOrderDto productionOrderDto);

    ProductionOrderMqttDto toMqttDto(ProductionOrderEntity productionOrderDto, boolean isEquipmentEnabled);

    ProductionOrderDto toDto(ProductionOrderEntity entity);

    default List<ProductionOrderDto> toDto(List<ProductionOrderEntity> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

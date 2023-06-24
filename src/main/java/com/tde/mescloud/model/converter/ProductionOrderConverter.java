package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderMqttDto;
import com.tde.mescloud.model.entity.ProductionOrderEntity;

public interface ProductionOrderConverter {

    ProductionOrderEntity toEntity(ProductionOrderDto productionOrderDto);

    ProductionOrderMqttDto toMqttDto(ProductionOrderEntity productionOrderDto);

    ProductionOrderDto toDto(ProductionOrderEntity entity);
}

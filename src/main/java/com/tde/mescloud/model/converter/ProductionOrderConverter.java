package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.entity.ProductionOrderEntity;

public interface ProductionOrderConverter {
    ProductionOrder convertToDomainObject(ProductionOrderEntity entity);

    ProductionOrderDto convertToDto(ProductionOrder productionOrder);

    ProductionOrderEntity convertToEntity(ProductionOrderDto productionOrderDto);
}

package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.ProductionOrderDto;
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
        productionOrderEntity.setCode(productionOrderEntity.getCode());
        productionOrderEntity.setTargetAmount(productionOrderDto.getTargetAmount());
        productionOrderEntity.setCreatedAt(productionOrderDto.getCreatedAt());
        //TODO: Implement
//        productionOrderEntity.setEquipment();

        return productionOrderEntity;
    }
}

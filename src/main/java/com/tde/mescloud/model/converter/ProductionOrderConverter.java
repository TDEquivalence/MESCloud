package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.entity.ProductionOrderEntity;

public interface ProductionOrderConverter {
    ProductionOrder convertToDO(ProductionOrderEntity entity);
}

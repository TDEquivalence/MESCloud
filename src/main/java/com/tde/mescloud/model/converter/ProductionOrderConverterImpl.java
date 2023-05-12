package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class ProductionOrderConverterImpl implements ProductionOrderConverter {


    @Override
    public ProductionOrder convertToDO(ProductionOrderEntity entity) {
        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setId(entity.getId());
        return productionOrder;
    }
}

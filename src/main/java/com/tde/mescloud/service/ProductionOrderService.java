package com.tde.mescloud.service;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.ProductionOrderDto;

public interface ProductionOrderService {

    ProductionOrder findByCode(String code);

    String generateCode();

    ProductionOrder save(ProductionOrderDto productionOrderDto);
}

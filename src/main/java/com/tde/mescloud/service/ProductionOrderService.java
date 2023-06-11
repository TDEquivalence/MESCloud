package com.tde.mescloud.service;

import com.tde.mescloud.model.ProductionOrder;

public interface ProductionOrderService {

    ProductionOrder findByCode(String code);

    String generateCode();
}

package com.tde.mescloud.service;

import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.ProductionOrderDto;

import java.util.Optional;

public interface ProductionOrderService {

    ProductionOrder findByCode(String code);

    String generateCode();

    ProductionOrder save(ProductionOrderDto productionOrderDto);

    boolean hasActiveProductionOrder(long countingEquipmentId);

    Optional<ProductionOrder> complete(long countingEquipmentId);
}

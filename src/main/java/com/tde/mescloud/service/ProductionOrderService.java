package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.ProductionOrderDto;

import java.util.Optional;

public interface ProductionOrderService {

    ProductionOrderDto findByCode(String code);

    String generateCode();

    ProductionOrderDto save(ProductionOrderDto productionOrderDto);

    boolean hasActiveProductionOrder(long countingEquipmentId);

    Optional<ProductionOrderDto> complete(long countingEquipmentId);
}

package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.ProductionOrderDto;

import java.util.Optional;

public interface ProductionOrderService {

    Optional<ProductionOrderDto> findByCode(String code);

    String generateCode();

    Optional<ProductionOrderDto> save(ProductionOrderDto productionOrderDto);

    boolean hasActiveProductionOrder(long countingEquipmentId);

    Optional<ProductionOrderDto> complete(long countingEquipmentId);
}

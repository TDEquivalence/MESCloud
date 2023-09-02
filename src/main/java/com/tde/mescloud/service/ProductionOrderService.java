package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.entity.ProductionOrderEntity;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderService {

    Optional<ProductionOrderDto> findByCode(String code);

    String generateCode();

    Optional<ProductionOrderDto> save(ProductionOrderDto productionOrderDto);

    boolean hasActiveProductionOrder(long countingEquipmentId);

    Optional<ProductionOrderDto> complete(long countingEquipmentId);

    ProductionOrderEntity saveAndUpdate(ProductionOrderEntity productionOrder);

    void delete(ProductionOrderEntity productionOrder);

    Optional<ProductionOrderEntity> findById(Long id);

    List<Long> checkOrderIdsExistInDatabase(List<Long> orderIds);
}

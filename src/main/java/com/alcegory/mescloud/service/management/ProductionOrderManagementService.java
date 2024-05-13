package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface ProductionOrderManagementService {

    Optional<ProductionOrderDto> create(RequestProductionOrderDto productionOrder, Authentication authentication);

    Optional<ProductionOrderDto> complete(long equipmentId, Authentication authentication);

    ProductionOrderDto editProductionOrder(ProductionOrderDto requestProductionOrder, Authentication authentication);
}

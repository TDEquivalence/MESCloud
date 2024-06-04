package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface ProductionOrderManagementService {

    Optional<ProductionOrderDto> create(String companyPrefix, String sectionPrefix, long sectionId, RequestProductionOrderDto productionOrder,
                                        Authentication authentication);

    Optional<ProductionOrderDto> complete(String companyPrefix, String sectionPrefix, long sectionId, long equipmentId, Authentication authentication);

    ProductionOrderDto editProductionOrder(ProductionOrderDto requestProductionOrder, Authentication authentication, long sectionId);
}

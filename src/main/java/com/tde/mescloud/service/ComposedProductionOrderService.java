package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;

import java.util.Optional;

public interface ComposedProductionOrderService {

    Optional<ComposedProductionOrderDto> create(ProductionOrderDto[] requestComposedArticleDto);
}

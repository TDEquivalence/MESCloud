package com.tde.mescloud.service;

import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.RequestSampleDto;

import java.util.Optional;

public interface ComposedProductionOrderService {

    Optional<ComposedProductionOrderDto> create(ProductionOrderDto[] requestComposedArticleDto);

    Optional<ComposedProductionOrderDto> createSample(RequestSampleDto requestSampleDto);
}

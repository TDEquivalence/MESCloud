package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ProductionOrderSummaryDto;
import com.tde.mescloud.model.entity.ProductionOrderSummaryEntity;

import java.util.ArrayList;
import java.util.List;

public interface ProductionOrderSummaryConverter {

    ProductionOrderSummaryEntity toEntity(ProductionOrderSummaryDto dto);

    ProductionOrderSummaryDto toDto(ProductionOrderSummaryEntity entity);

    default List<ProductionOrderSummaryDto> toDto(List<ProductionOrderSummaryEntity> entities) {
        List<ProductionOrderSummaryDto> dtos = new ArrayList<>(entities.size());
        entities.forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    default List<ProductionOrderSummaryEntity> toEntity(List<ProductionOrderSummaryDto> dtos) {
        List<ProductionOrderSummaryEntity> entities = new ArrayList<>(dtos.size());
        dtos.forEach(dto -> entities.add(toEntity(dto)));
        return entities;
    }
}

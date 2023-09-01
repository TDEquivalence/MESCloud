package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;

import java.util.List;

public interface ComposedProductionOrderConverter {

    public ComposedProductionOrderDto convertToDto(ComposedProductionOrderEntity factoryEntity);

    public ComposedProductionOrderEntity convertToEntity(ComposedProductionOrderDto factoryDto);

    public List<ComposedProductionOrderDto> convertToDto(List<ComposedProductionOrderEntity> factoryEntityList);

    public List<ComposedProductionOrderEntity> convertToEntity(List<ComposedProductionOrderDto> factoryDtoList);
}

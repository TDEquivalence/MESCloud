package com.tde.mescloud.model.converter;


import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;

import java.util.List;

public interface ComposedProductionOrderConverter {

    public ComposedProductionOrderDto convertToDto(ComposedProductionOrderEntity composedEntity);

    public ComposedProductionOrderEntity convertToEntity(ComposedProductionOrderDto composedEntity);

    public List<ComposedProductionOrderDto> convertToDto(List<ComposedProductionOrderEntity> composedEntityList);

    public List<ComposedProductionOrderEntity> convertToEntity(List<ComposedProductionOrderDto> composedDtoList);
}

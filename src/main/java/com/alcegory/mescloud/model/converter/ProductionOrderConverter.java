package com.alcegory.mescloud.model.converter;


import com.alcegory.mescloud.model.dto.production.*;
import com.alcegory.mescloud.model.entity.production.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;

import java.util.List;

public interface ProductionOrderConverter {

    ProductionOrderEntity toEntity(ProductionOrderDto productionOrderDto);

    ProductionOrderEntity toEntity(RequestProductionOrderDto productionOrderDto);

    ProductionOrderMqttDto toMqttDto(ProductionOrderEntity productionOrderDto, boolean isEquipmentEnabled);

    ProductionOrderDto toDto(ProductionOrderEntity entity);

    ProductionOrderInfoDto toInfoDto(ProductionOrderEntity entity);

    List<ProductionInstructionDto> toDtoList(List<ProductionInstructionEntity> entities);

    List<ProductionOrderExportInfoDto> toExportDtoList(List<ProductionOrderEntity> entities);

    default List<ProductionOrderDto> toDto(List<ProductionOrderEntity> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .toList();
    }
}

package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ProductionOrderSummaryDto;
import com.tde.mescloud.model.entity.ProductionOrderSummaryEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductionOrderSummaryConverterImpl implements ProductionOrderSummaryConverter {

    private ModelMapper modelMapper;

    @Override
    public ProductionOrderSummaryEntity toEntity(ProductionOrderSummaryDto dto) {
        return (dto == null) ? null : modelMapper.map(dto, ProductionOrderSummaryEntity.class);
    }

    @Override
    public ProductionOrderSummaryDto toDto(ProductionOrderSummaryEntity entity) {
        return (entity == null) ? null : modelMapper.map(entity, ProductionOrderSummaryDto.class);
    }
}

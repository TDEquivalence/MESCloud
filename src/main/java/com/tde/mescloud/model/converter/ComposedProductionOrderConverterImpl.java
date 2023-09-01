package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ComposedProductionOrderDto;
import com.tde.mescloud.model.entity.ComposedProductionOrderEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ComposedProductionOrderConverterImpl implements ComposedProductionOrderConverter {

    private final ModelMapper mapper;

    public ComposedProductionOrderDto convertToDto(ComposedProductionOrderEntity composedProductionOrderEntity) {
        return mapper.map(composedProductionOrderEntity, ComposedProductionOrderDto.class);
    }

    public ComposedProductionOrderEntity convertToEntity(ComposedProductionOrderDto composedProductionOrderDto) {
        return (composedProductionOrderDto == null) ? null : mapper.map(composedProductionOrderDto, ComposedProductionOrderEntity.class);
    }

    public List<ComposedProductionOrderDto> convertToDto(List<ComposedProductionOrderEntity> factoryEntityList) {
        return factoryEntityList.stream().map(this::convertToDto).toList();
    }

    public List<ComposedProductionOrderEntity> convertToEntity(List<ComposedProductionOrderDto> factoryDtoList) {
        return factoryDtoList.stream().map(this::convertToEntity).toList();
    }
}

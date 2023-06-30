package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.FactoryDto;
import com.tde.mescloud.model.entity.FactoryEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class FactoryConverter {

    private final ModelMapper mapper;

    public FactoryDto convertToDto(FactoryEntity factoryEntity) {
        return mapper.map(factoryEntity, FactoryDto.class);
    }

    public FactoryEntity convertToEntity(FactoryDto factoryDto) {
        return (factoryDto == null) ? null : mapper.map(factoryDto, FactoryEntity.class);
    }

    public List<FactoryDto> convertToDto(List<FactoryEntity> factoryEntityList) {
        return factoryEntityList.stream().map(this::convertToDto).toList();
    }

    public List<FactoryEntity> convertToEntity(List<FactoryDto> factoryDtoList) {
        return factoryDtoList.stream().map(this::convertToEntity).toList();
    }
}

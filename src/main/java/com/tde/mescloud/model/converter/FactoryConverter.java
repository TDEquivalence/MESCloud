package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.FactoryDto;
import com.tde.mescloud.model.entity.FactoryEntity;

import java.util.List;

public interface FactoryConverter {

    public FactoryDto convertToDto(FactoryEntity factoryEntity);

    public FactoryEntity convertToEntity(FactoryDto factoryDto);

    public List<FactoryDto> convertToDto(List<FactoryEntity> factoryEntityList);

    public List<FactoryEntity> convertToEntity(List<FactoryDto> factoryDtoList);
}

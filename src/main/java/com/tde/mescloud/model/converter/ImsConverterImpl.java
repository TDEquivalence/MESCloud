package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.ImsDto;
import com.tde.mescloud.model.entity.ImsEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImsConverterImpl implements ImsConverter {

    private ModelMapper modelMapper;

    @Override
    public ImsEntity toEntity(ImsDto dto) {
        return (dto == null) ? null : modelMapper.map(dto, ImsEntity.class);
    }

    @Override
    public ImsDto toDto(ImsEntity entity) {
        return (entity == null) ? null : modelMapper.map(entity, ImsDto.class);
    }
}

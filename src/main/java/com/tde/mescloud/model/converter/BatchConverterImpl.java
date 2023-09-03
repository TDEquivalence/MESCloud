package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.BatchDto;
import com.tde.mescloud.model.entity.BatchEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BatchConverterImpl implements BatchConverter {

    private final ModelMapper modelMapper;

    @Override
    public BatchEntity toEntity(BatchDto dto) {
        return (dto == null) ? null : modelMapper.map(dto, BatchEntity.class);
    }

    @Override
    public BatchDto toDto(BatchEntity entity) {
        return (entity == null) ? null : modelMapper.map(entity, BatchDto.class);
    }
}

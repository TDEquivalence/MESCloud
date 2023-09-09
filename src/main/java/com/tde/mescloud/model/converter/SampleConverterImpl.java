package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.SampleDto;
import com.tde.mescloud.model.entity.SampleEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SampleConverterImpl implements SampleConverter {

    private final ModelMapper mapper;

    @Override
    public SampleDto convertToDto(SampleEntity sampleEntity) {
        return mapper.map(sampleEntity, SampleDto.class);
    }

    @Override
    public SampleEntity convertToEntity(SampleDto sampleDto) {
        return (sampleDto == null) ? null : mapper.map(sampleDto, SampleEntity.class);
    }

    @Override
    public List<SampleDto> convertToDto(List<SampleEntity> sampleEntityList) {
        return sampleEntityList.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<SampleEntity> convertToEntity(List<SampleDto> sampleDtoList) {
        return sampleDtoList.stream().map(this::convertToEntity).toList();
    }
}

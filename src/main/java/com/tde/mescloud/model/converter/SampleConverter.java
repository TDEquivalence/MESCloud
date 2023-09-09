package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.SampleDto;
import com.tde.mescloud.model.entity.SampleEntity;

import java.util.List;

public interface SampleConverter {

    public SampleDto convertToDto(SampleEntity sampleEntity);

    public SampleEntity convertToEntity(SampleDto sampleDto);

    public List<SampleDto> convertToDto(List<SampleEntity> sampleEntityList);

    public List<SampleEntity> convertToEntity(List<SampleDto> sampleDtoList);
}

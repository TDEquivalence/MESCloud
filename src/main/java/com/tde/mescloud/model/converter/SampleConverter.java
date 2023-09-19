package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.SampleDto;
import com.tde.mescloud.model.entity.SampleEntity;

import java.util.List;

public interface SampleConverter {

    SampleDto convertToDto(SampleEntity sampleEntity);

    SampleEntity convertToEntity(SampleDto sampleDto);

    List<SampleDto> convertToDto(List<SampleEntity> sampleEntityList);

    List<SampleEntity> convertToEntity(List<SampleDto> sampleDtoList);
}

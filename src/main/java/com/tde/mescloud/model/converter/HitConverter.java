package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.HitDto;
import com.tde.mescloud.model.entity.HitEntity;

import java.util.List;

public interface HitConverter {

    public HitDto convertToDto(HitEntity sampleEntity);

    public HitEntity convertToEntity(HitDto sampleDto);

    public List<HitDto> convertToDto(List<HitEntity> sampleEntityList);

    public List<HitEntity> convertToEntity(List<HitDto> sampleDtoList);
}

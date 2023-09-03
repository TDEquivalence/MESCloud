package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.HitDto;
import com.tde.mescloud.model.entity.HitEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class HitConverterImpl implements HitConverter {

    private final ModelMapper mapper;

    @Override
    public HitDto convertToDto(HitEntity hitEntity) {
        return mapper.map(hitEntity, HitDto.class);
    }

    @Override
    public HitEntity convertToEntity(HitDto hitDto) {
        return (hitDto == null) ? null : mapper.map(hitDto, HitEntity.class);
    }

    @Override
    public List<HitDto> convertToDto(List<HitEntity> hitEntityList) {
        return hitEntityList.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<HitEntity> convertToEntity(List<HitDto> hitDtoList) {
        return hitDtoList.stream().map(this::convertToEntity).toList();
    }
}

package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.dto.SampleDto;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.SampleEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
@AllArgsConstructor
public class EquipmentOutputConverterImpl implements EquipmentOutputConverter {

    private final ModelMapper mapper;

    @Override
    public EquipmentOutputDto toDto(EquipmentOutputEntity entity) {
        return mapper.map(entity, EquipmentOutputDto.class);
    }

    @Override
    public EquipmentOutputEntity convertToEntity(EquipmentOutputDto equipmentOutputDto) {
        return (equipmentOutputDto == null) ? null : mapper.map(equipmentOutputDto, EquipmentOutputEntity.class);
    }

    @Override
    public List<EquipmentOutputDto> toDto(List<EquipmentOutputEntity> equipmentOutputEntityList) {
        return equipmentOutputEntityList.stream().map(this::toDto).toList();
    }

    @Override
    public List<EquipmentOutputEntity> convertToEntity(List<EquipmentOutputDto> equipmentOutputDtoList) {
        return equipmentOutputDtoList.stream().map(this::convertToEntity).toList();
    }


}

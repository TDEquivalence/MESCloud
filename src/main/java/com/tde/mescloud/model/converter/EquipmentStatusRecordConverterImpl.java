package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.EquipmentStatusRecordDto;
import com.tde.mescloud.model.entity.EquipmentStatusRecordEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EquipmentStatusRecordConverterImpl implements EquipmentStatusRecordConverter {

    private ModelMapper modelMapper;

    @Override
    public EquipmentStatusRecordDto toDto(EquipmentStatusRecordEntity entity) {
        return modelMapper.map(entity, EquipmentStatusRecordDto.class);
    }

    @Override
    public EquipmentStatusRecordEntity toEntity(EquipmentStatusRecordDto equipmentOutputDto) {
        return (equipmentOutputDto == null) ? null : modelMapper.map(equipmentOutputDto, EquipmentStatusRecordEntity.class);
    }

    @Override
    public List<EquipmentStatusRecordDto> toDto(List<EquipmentStatusRecordEntity> equipmentOutputEntityList) {
        return equipmentOutputEntityList.stream().map(this::toDto).toList();
    }

    @Override
    public List<EquipmentStatusRecordEntity> toEntity(List<EquipmentStatusRecordDto> equipmentOutputDtoList) {
        return equipmentOutputDtoList.stream().map(this::toEntity).toList();
    }
}

package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.entity.CounterRecordConclusionEntity;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
@AllArgsConstructor
public class CounterRecordConverterImpl implements CounterRecordConverter {

    private final ModelMapper mapper;


    public CounterRecordDto toDto(CounterRecordEntity entity) {
        CounterRecordDto counterRecordDto = mapper.map(entity, CounterRecordDto.class);
        if (entity.getEquipmentOutput() != null && entity.getEquipmentOutput().getCountingEquipment() != null) {
            counterRecordDto.setEquipmentAlias(entity.getEquipmentOutput().getCountingEquipment().getAlias());
        }
        return counterRecordDto;
    }

    @Override
    public CounterRecordDto conclusionViewToDto(CounterRecordConclusionEntity entity) {
        CounterRecordDto dto = mapper.map(entity, CounterRecordDto.class);
        //TODO: Learn how to do it with the mapper
        dto.setEquipmentAlias(entity.getEquipmentOutput().getCountingEquipment().getAlias());
        dto.setValidForProduction(entity.getEquipmentOutput().isValidForProduction());
        return dto;
    }

    @Override
    public CounterRecordDto convertToDto(CounterRecordEntity entity) {
        return mapper.map(entity, CounterRecordDto.class);
    }

    @Override
    public CounterRecordEntity convertToEntity(CounterRecordDto dto) {
        return (dto == null) ? null : mapper.map(dto, CounterRecordEntity.class);
    }

    @Override
    public List<CounterRecordDto> convertToDto(List<CounterRecordEntity> entityList) {
        return entityList.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<CounterRecordEntity> convertToEntity(List<CounterRecordDto> dtoList) {
        return dtoList.stream().map(this::convertToEntity).toList();
    }
}

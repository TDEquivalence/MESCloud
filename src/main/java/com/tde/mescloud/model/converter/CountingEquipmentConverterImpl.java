package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.repository.CountingEquipmentProjection;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
@AllArgsConstructor
public class CountingEquipmentConverterImpl implements CountingEquipmentConverter {

    private final EquipmentOutputConverter equipmentOutputConverter;
    private final ModelMapper mapper;


    @Override
    public CountingEquipmentDto toDto(CountingEquipmentEntity entity) {

        CountingEquipmentDto countingEquipmentDto = new CountingEquipmentDto();
        countingEquipmentDto.setId(entity.getId());
        countingEquipmentDto.setCode(entity.getCode());
        countingEquipmentDto.setAlias(entity.getAlias());
        countingEquipmentDto.setEquipmentStatus(entity.getEquipmentStatus());
        countingEquipmentDto.setPTimerCommunicationCycle(entity.getPTimerCommunicationCycle());

        List<EquipmentOutputDto> equipmentOutputDtos = equipmentOutputConverter.toDto(entity.getOutputs());
        countingEquipmentDto.setOutputs(equipmentOutputDtos);

        return countingEquipmentDto;
    }

    @Override
    public CountingEquipmentDto toDto(CountingEquipmentProjection projection) {

        CountingEquipmentDto dto = new CountingEquipmentDto();
        dto.setId(projection.getId());
        dto.setCode(projection.getCode());
        dto.setAlias(projection.getAlias());
        dto.setEquipmentStatus(projection.getEquipmentStatus());
        dto.setPTimerCommunicationCycle(projection.getPTimerCommunicationCycle());
        dto.setProductionOrderCode(projection.getProductionOrderCode());

//        List<EquipmentOutputDto> outputs = equipmentOutputConverter.toDto(projection.getOutputs());
//        dto.setOutputs(outputs);

        return dto;
    }

    @Override
    public CountingEquipmentEntity toEntity(CountingEquipmentDto dto) {

        CountingEquipmentEntity entity = new CountingEquipmentEntity();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setEquipmentStatus(dto.getEquipmentStatus());
        entity.setAlias(dto.getAlias());
        entity.setPTimerCommunicationCycle(dto.getPTimerCommunicationCycle());

//        if (dto.getOutputs() != null ) {
//            equipmentOutputConverter.toEntity(dto.getOutputs());
//        }
//        entity.setOutputs();

        return entity;
    }


    public CountingEquipmentDto convertToDto(CountingEquipmentEntity factoryEntity) {
        return mapper.map(factoryEntity, CountingEquipmentDto.class);
    }

    public CountingEquipmentEntity convertToEntity(CountingEquipmentDto factoryDto) {
        return (factoryDto == null) ? null : mapper.map(factoryDto, CountingEquipmentEntity.class);
    }

    public List<CountingEquipmentDto> convertToDto(List<CountingEquipmentEntity> factoryEntityList) {
        return factoryEntityList.stream().map(this::convertToDto).toList();
    }

    public List<CountingEquipmentEntity> convertToEntity(List<CountingEquipmentDto> factoryDtoList) {
        return factoryDtoList.stream().map(this::convertToEntity).toList();
    }
}

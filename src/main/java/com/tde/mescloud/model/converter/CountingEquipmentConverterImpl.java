package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.RequestConfigurationDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
@AllArgsConstructor
public class CountingEquipmentConverterImpl implements CountingEquipmentConverter {

    private final ModelMapper mapper;


    public CountingEquipmentDto convertToDto(CountingEquipmentEntity countingEquipment) {
        return mapper.map(countingEquipment, CountingEquipmentDto.class);
    }

    public CountingEquipmentEntity convertToEntity(CountingEquipmentDto countingEquipment) {
        return (countingEquipment == null) ? null : mapper.map(countingEquipment, CountingEquipmentEntity.class);
    }

    public List<CountingEquipmentDto> convertToDto(List<CountingEquipmentEntity> countingEquipments) {
        return countingEquipments.stream().map(this::convertToDto).toList();
    }

    public List<CountingEquipmentEntity> convertToEntity(List<CountingEquipmentDto> countingEquipments) {
        return countingEquipments.stream().map(this::convertToEntity).toList();
    }

    public CountingEquipmentEntity convertToEntity(RequestConfigurationDto request) {
        return (request == null) ? null : mapper.map(request, CountingEquipmentEntity.class);
    }
}

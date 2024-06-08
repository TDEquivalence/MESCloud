package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentSummaryDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.request.RequestConfigurationDto;
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

    public CountingEquipmentSummaryDto convertToSummaryDto(CountingEquipmentEntity countingEquipment) {
        return mapper.map(countingEquipment, CountingEquipmentSummaryDto.class);
    }

    public CountingEquipmentEntity convertToEntity(CountingEquipmentSummaryDto summaryDto) {
        return (summaryDto == null) ? null : mapper.map(summaryDto, CountingEquipmentEntity.class);
    }

    public List<CountingEquipmentSummaryDto> convertToSummaryDtoList(List<CountingEquipmentEntity> countingEquipments) {
        return countingEquipments.stream()
                .map(this::convertToSummaryDto)
                .toList();
    }

    public List<CountingEquipmentEntity> convertToEntityList(List<CountingEquipmentSummaryDto> summaryDtos) {
        return summaryDtos.stream()
                .map(this::convertToEntity)
                .toList();
    }
}

package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;

import java.util.ArrayList;
import java.util.List;

public interface CountingEquipmentConverter {

    CountingEquipment convertToDomainObject(CountingEquipmentEntity countingEquipmentEntity);

    default List<CountingEquipment> convertToDomainObject(Iterable<CountingEquipmentEntity> countingEquipmentEntities) {

        List<CountingEquipment> countingEquipments = new ArrayList<>();
        for (CountingEquipmentEntity countingEquipmentEntity : countingEquipmentEntities) {
            CountingEquipment countingEquipment = convertToDomainObject(countingEquipmentEntity);
            countingEquipments.add(countingEquipment);
        }

        return countingEquipments;
    }

    CountingEquipmentDto convertToDto(CountingEquipment countingEquipment);

    default List<CountingEquipmentDto> convertToDto(Iterable<CountingEquipment> countingEquipments) {

        List<CountingEquipmentDto> countingEquipmentDtos = new ArrayList<>();
        for (CountingEquipment countingEquipment : countingEquipments) {
            CountingEquipmentDto countingEquipmentDto = convertToDto(countingEquipment);
            countingEquipmentDtos.add(countingEquipmentDto);
        }

        return countingEquipmentDtos;
    }
}

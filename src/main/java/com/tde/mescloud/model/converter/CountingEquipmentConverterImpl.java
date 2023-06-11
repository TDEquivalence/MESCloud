package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class CountingEquipmentConverterImpl implements CountingEquipmentConverter {

    public CountingEquipment convertToDomainObject(CountingEquipmentEntity countingEquipmentEntity) {
        CountingEquipment countingEquipment = new CountingEquipment();
        countingEquipment.setId(countingEquipmentEntity.getId());
        countingEquipment.setAlias(countingEquipmentEntity.getAlias());
        countingEquipment.setCode(countingEquipmentEntity.getCode());
        countingEquipment.setPTimerCommunicationCycle(countingEquipmentEntity.getPTimerCommunicationCycle());
        countingEquipment.setEquipmentStatus(countingEquipmentEntity.getEquipmentStatus());

        //TODO: Implement converter
//        countingEquipment.setOutputs(countingEquipmentEntity.getOutputs());
        return countingEquipment;
    }

    public CountingEquipmentDto convertToDto(CountingEquipment countingEquipment) {
        CountingEquipmentDto countingEquipmentDto = new CountingEquipmentDto();
        countingEquipmentDto.setId(countingEquipment.getId());
        countingEquipmentDto.setAlias(countingEquipment.getAlias());
        countingEquipmentDto.setCode(countingEquipment.getCode());
        countingEquipmentDto.setPTimerCommunicationCycle(countingEquipment.getPTimerCommunicationCycle());
        countingEquipmentDto.setEquipmentStatus(countingEquipment.getEquipmentStatus());

        //TODO: Implement converter
//        countingEquipmentDto.setOutputs(countingEquipment.getOutputs());
        return countingEquipmentDto;
    }
}

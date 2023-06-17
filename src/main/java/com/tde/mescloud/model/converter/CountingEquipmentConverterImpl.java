package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CountingEquipment;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.model.dto.EquipmentOutputDto;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
@AllArgsConstructor
public class CountingEquipmentConverterImpl implements CountingEquipmentConverter {

    private final EquipmentOutputConverter equipmentOutputConverter;

    public CountingEquipment convertToDomainObject(CountingEquipmentEntity countingEquipmentEntity) {
        CountingEquipment countingEquipment = new CountingEquipment();
        countingEquipment.setId(countingEquipmentEntity.getId());
        countingEquipment.setAlias(countingEquipmentEntity.getAlias());
        countingEquipment.setCode(countingEquipmentEntity.getCode());
        countingEquipment.setPTimerCommunicationCycle(countingEquipmentEntity.getPTimerCommunicationCycle());
        countingEquipment.setEquipmentStatus(countingEquipmentEntity.getEquipmentStatus());

        List<EquipmentOutput> equipmentOutputs = equipmentOutputConverter.convertToDomainObject(countingEquipmentEntity.getOutputs());
        countingEquipment.setOutputs(equipmentOutputs);
        return countingEquipment;
    }

    public CountingEquipmentDto convertToDto(CountingEquipment countingEquipment) {
        CountingEquipmentDto countingEquipmentDto = new CountingEquipmentDto();
        countingEquipmentDto.setId(countingEquipment.getId());
        countingEquipmentDto.setAlias(countingEquipment.getAlias());
        countingEquipmentDto.setCode(countingEquipment.getCode());
        countingEquipmentDto.setPTimerCommunicationCycle(countingEquipment.getPTimerCommunicationCycle());
        countingEquipmentDto.setEquipmentStatus(countingEquipment.getEquipmentStatus());
        countingEquipmentDto.setHasActiveProductionOrder(countingEquipment.isHasActiveProductionOrder());

        List<EquipmentOutputDto> equipmentOutputDtos = equipmentOutputConverter.convertToDto(countingEquipment.getOutputs());
        countingEquipmentDto.setOutputs(equipmentOutputDtos);

        return countingEquipmentDto;
    }

    @Override
    public CountingEquipmentEntity convertToEntity(CountingEquipmentDto countingEquipmentDto) {
        CountingEquipmentEntity countingEquipmentEntity = new CountingEquipmentEntity();
        countingEquipmentEntity.setId(countingEquipmentDto.getId());
        countingEquipmentEntity.setEquipmentStatus(countingEquipmentDto.getEquipmentStatus());
        countingEquipmentEntity.setCode(countingEquipmentDto.getCode());
        countingEquipmentEntity.setAlias(countingEquipmentDto.getAlias());
        countingEquipmentEntity.setPTimerCommunicationCycle(countingEquipmentDto.getPTimerCommunicationCycle());
        return countingEquipmentEntity;
    }
}

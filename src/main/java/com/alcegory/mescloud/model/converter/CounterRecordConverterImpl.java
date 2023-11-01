package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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
        dto.setEquipmentAlias(entity.getEquipmentOutput().getCountingEquipment().getAlias());
        return dto;
    }
}

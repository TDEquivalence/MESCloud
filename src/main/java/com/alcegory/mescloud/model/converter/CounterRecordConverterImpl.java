package com.alcegory.mescloud.model.converter;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import com.alcegory.mescloud.model.entity.CounterRecordConclusionEntity;
import com.alcegory.mescloud.model.entity.CounterRecordEntity;
import com.alcegory.mescloud.model.entity.ImsEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

        dto.setEquipmentAlias(Optional.ofNullable(entity.getEquipmentOutput())
                .map(output -> output.getCountingEquipment().getAlias())
                .orElse(null));

        dto.setIms(Optional.ofNullable(entity.getProductionOrder())
                .map(ProductionOrderEntity::getIms)
                .map(ImsEntity::getCode)
                .orElse(null));

        dto.setInputBatch(Optional.ofNullable(entity.getProductionOrder())
                .map(ProductionOrderEntity::getInputBatch)
                .orElse(null));

        dto.setSource(Optional.ofNullable(entity.getProductionOrder())
                .map(ProductionOrderEntity::getSource)
                .orElse(null));

        dto.setGauge(Optional.ofNullable(entity.getProductionOrder())
                .map(ProductionOrderEntity::getGauge)
                .orElse(null));

        dto.setCategory(Optional.ofNullable(entity.getProductionOrder())
                .map(ProductionOrderEntity::getCategory)
                .orElse(null));

        dto.setWashingProcess(Optional.ofNullable(entity.getProductionOrder())
                .map(ProductionOrderEntity::getWashingProcess)
                .orElse(null));

        return dto;
    }
}

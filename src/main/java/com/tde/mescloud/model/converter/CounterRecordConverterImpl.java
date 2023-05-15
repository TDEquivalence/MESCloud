package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.ProductionOrder;
import com.tde.mescloud.model.dto.CounterMqttDto;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
public class CounterRecordConverterImpl implements CounterRecordConverter {

    private final ModelMapper modelMapper;

    public CounterRecordConverterImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CounterRecordDto convertToDTO(CounterRecord counterRecord) {
        return modelMapper.map(counterRecord, CounterRecordDto.class);
    }

    @Override
    public List<CounterRecordDto> convertToDTO(List<CounterRecord> counterRecords) {
        return counterRecords.stream().map(this::convertToDTO).toList();
    }

    @Override
    public CounterRecord convertToDO(EquipmentCountsMqttDto equipmentCountsDTO, CounterMqttDto counterDTO) {
        CounterRecord counterRecord = new CounterRecord();
        counterRecord.setEquipmentCode(equipmentCountsDTO.getEquipmentCode());
        counterRecord.setRealValue(counterDTO.getValue());

        EquipmentOutput equipmentOutput = new EquipmentOutput();
        equipmentOutput.setCode(counterDTO.getOutputCode());
        counterRecord.setEquipmentOutput(equipmentOutput);

        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setCode(equipmentCountsDTO.getProductionOrderCode());
        counterRecord.setProductionOrder(productionOrder);

        return counterRecord;
    }

    @Override
    public CounterRecord convertToDO(CounterRecordEntity entity) {
        return modelMapper.map(entity, CounterRecord.class);
    }

    @Override
    public CounterRecordEntity convertToEntity(CounterRecord counterRecord) {
        CounterRecordEntity counterRecordEntity = new CounterRecordEntity();
        counterRecordEntity.setRealValue(counterRecord.getRealValue());
        counterRecordEntity.setComputedValue(counterRecord.getComputedValue());
        counterRecordEntity.setRegisteredAt(counterRecord.getRegisteredAt());

        EquipmentOutputEntity equipmentOutputEntity = new EquipmentOutputEntity();
        if (counterRecord.getEquipmentOutput() != null) {
            counterRecordEntity.setEquipmentOutputAlias(counterRecord.getEquipmentOutput().getAlias());
            equipmentOutputEntity.setId(counterRecord.getEquipmentOutput().getId());
        }
        counterRecordEntity.setEquipmentOutput(equipmentOutputEntity);

        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        if (counterRecord.getProductionOrder() != null) {
            productionOrderEntity.setId(counterRecord.getProductionOrder().getId());
        }
        counterRecordEntity.setProductionOrder(productionOrderEntity);

        return counterRecordEntity;
    }
}

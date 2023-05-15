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
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
public class CounterRecordConverterImpl implements CounterRecordConverter {

    public CounterRecordDto convertToDto(CounterRecord counterRecord) {
        CounterRecordDto counterRecordDto = new CounterRecordDto();
        counterRecordDto.setId(counterRecord.getId());
        counterRecordDto.setRegisteredAt(counterRecord.getRegisteredAt());
        counterRecordDto.setComputedValue(counterRecord.getComputedValue());
        counterRecordDto.setEquipmentAlias(counterRecord.getEquipmentOutput().getCountingEquipment().getAlias());
        counterRecordDto.setEquipmentOutputAlias(counterRecord.getEquipmentOutput().getAlias());
        counterRecordDto.setProductionOrderCode(counterRecord.getProductionOrder().getCode());
        return counterRecordDto;
    }

    @Override
    public List<CounterRecordDto> convertToDto(List<CounterRecord> counterRecords) {
        return counterRecords.stream().map(this::convertToDto).toList();
    }

    @Override
    public CounterRecord convertToDomainObj(EquipmentCountsMqttDto equipmentCountsDTO, CounterMqttDto counterDTO) {
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
    public CounterRecord convertToDomainObj(CounterRecordEntity entity) {
        CounterRecord counterRecord = new CounterRecord();
        counterRecord.setId(entity.getId());
        counterRecord.setEquipmentCode(entity.getEquipmentOutput().getCode());
        counterRecord.setRealValue(entity.getRealValue());
        counterRecord.setComputedValue(entity.getComputedValue());
        counterRecord.setRegisteredAt(entity.getRegisteredAt());

        EquipmentOutput equipmentOutput = new EquipmentOutput(entity.getEquipmentOutput());
        counterRecord.setEquipmentOutput(equipmentOutput);

        ProductionOrder productionOrder = new ProductionOrder(entity.getProductionOrder());
        productionOrder.setId(entity.getProductionOrder().getId());
        productionOrder.setCode(entity.getProductionOrder().getCode());
        counterRecord.setProductionOrder(productionOrder);

        return counterRecord;
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

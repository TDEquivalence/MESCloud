package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.EquipmentOutput;
import com.tde.mescloud.model.dto.CounterMqttDTO;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class CounterRecordConverterImpl implements CounterRecordConverter {


    @Override
    public CounterRecord convertToDO(EquipmentCountsMqttDTO equipmentCountsDTO, CounterMqttDTO counterDTO) {
        CounterRecord counterRecord = new CounterRecord();
        counterRecord.setProductionOrderCode(equipmentCountsDTO.getProductionOrderCode());
        counterRecord.setEquipmentCode(equipmentCountsDTO.getEquipmentCode());

        EquipmentOutput equipmentOutput = new EquipmentOutput();
        equipmentOutput.setCode(counterDTO.getOutputCode());
        counterRecord.setEquipmentOutput(equipmentOutput);

        counterRecord.setRealValue(counterDTO.getValue());
        return counterRecord;
    }

    @Override
    public CounterRecord convertToDO(CounterRecordEntity entity) {
        return null;
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

        return counterRecordEntity;
    }
}

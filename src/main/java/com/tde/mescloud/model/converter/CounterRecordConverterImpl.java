package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.dto.CounterMqttDTO;
import com.tde.mescloud.model.dto.EquipmentCountsMqttDTO;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
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
        counterRecord.setEquipmentOutputCode(counterDTO.getOutputCode());
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
        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setCode(counterRecord.getProductionOrderCode());
        counterRecordEntity.setProductionOrder(productionOrderEntity);
        counterRecordEntity.setEquipmentOutputAlias(counterRecord.getEquipmentOutputAlias());

        EquipmentOutputEntity equipmentOutput = new EquipmentOutputEntity();
        equipmentOutput.setCode(counterRecord.getEquipmentOutputCode());
//        counterRecordEntity.setEquipmentOutput(equipmentOutput);
        counterRecordEntity.setRealValue(counterRecord.getRealValue());
        counterRecordEntity.setComputedValue(counterRecord.getComputedValue());
        counterRecordEntity.setRegisteredAt(counterRecord.getRegisteredAt());
        return counterRecordEntity;
    }
}

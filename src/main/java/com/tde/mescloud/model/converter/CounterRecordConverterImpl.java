package com.tde.mescloud.model.converter;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class CounterRecordConverterImpl implements CounterRecordConverter {


    @Override
    public CounterRecordDto toDto(CounterRecordEntity entity) {

        CounterRecordDto dto = new CounterRecordDto();
        dto.setId(entity.getId());
        dto.setRegisteredAt(entity.getRegisteredAt());
        dto.setComputedValue(entity.getComputedValue());
        dto.setEquipmentOutputAlias(entity.getEquipmentOutputAlias());

        if (entity.getProductionOrder() != null ) {
            dto.setProductionOrderCode(entity.getProductionOrder().getCode());
        }

        if (entity.getEquipmentOutput() != null && entity.getEquipmentOutput().getCountingEquipment() != null) {
            dto.setEquipmentAlias(entity.getEquipmentOutput().getCountingEquipment().getAlias());
        }

        return dto;
    }
}

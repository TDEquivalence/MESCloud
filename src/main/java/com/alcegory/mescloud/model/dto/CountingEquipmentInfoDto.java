package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountingEquipmentInfoDto {

    private CountingEquipmentDto countingEquipment;
    private ProductionOrderInfoDto productionOrder;
    private List<FeatureDto> features;
}
package com.alcegory.mescloud.model.dto.equipment;

import com.alcegory.mescloud.model.dto.company.FeatureDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderInfoDto;
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
package com.alcegory.mescloud.model.dto.production;

import com.alcegory.mescloud.model.dto.mqqt.AbstractMqttDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionOrderMqttDto extends AbstractMqttDto {

    private String equipmentCode;
    private String productionOrderCode;
    private int targetAmount;
    private boolean isEquipmentEnabled;
}

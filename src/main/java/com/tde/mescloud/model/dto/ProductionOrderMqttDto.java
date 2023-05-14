package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionOrderMqttDto extends AbstractMqttDto {

    private String productionOrderCode;
    private String equipmentCode;
    private int targetAmount;
    private boolean isEquipmentEnabled;
}

package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionOrderMqttDTO extends AbstractMqttDTO {

    private String productionOrderCode;
    private String equipmentCode;
    private int targetAmount;
    private boolean isEquipmentEnabled;
}

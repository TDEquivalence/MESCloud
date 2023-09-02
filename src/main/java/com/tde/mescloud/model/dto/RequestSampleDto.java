package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSampleDto {

    private ProductionOrderDto[] productionOrders;
    private int amount;
}

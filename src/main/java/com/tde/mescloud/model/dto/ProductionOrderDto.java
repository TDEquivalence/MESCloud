package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.CountingEquipment;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProductionOrderDto {

    private long id;
    private CountingEquipment equipment;
    private String code;
    private int targetAmount;
    private Date createdAt;
}

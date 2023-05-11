package com.tde.mescloud.model;

import com.tde.mescloud.model.entity.Equipment;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProductionOrder {

    private long id;
    private Equipment equipment;
    private String code;
    private int targetAmount;
    //TODO: Implement productionInstruction
    private Date createdAt;
}

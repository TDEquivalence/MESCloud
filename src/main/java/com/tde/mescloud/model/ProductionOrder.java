package com.tde.mescloud.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProductionOrder {

    private long id;
    private CountingEquipment equipment;
    private String code;
    private int targetAmount;
    //TODO: Implement productionInstruction
    private Date createdAt;

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
}

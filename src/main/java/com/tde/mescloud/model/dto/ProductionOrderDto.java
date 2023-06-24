package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProductionOrderDto {

    private long id;
    private long equipmentId;
    private String code;
    private int targetAmount;
    private Date createdAt;

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
}

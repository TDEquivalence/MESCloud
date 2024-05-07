package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ProductionOrderDto {

    private long id;
    private long equipmentId;
    private String code;
    private String imsCode;
    private int targetAmount;
    private Date createdAt;
    private Date completedAt;

    private Map<String, String> productionInstructions;
}

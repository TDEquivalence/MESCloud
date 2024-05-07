package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductionOrderDto {

    private long id;
    private long equipmentId;
    private String code;
    private int targetAmount;
    private Date createdAt;
    private Date completedAt;

    private List<ProductionInstructionDto> instructions;
}

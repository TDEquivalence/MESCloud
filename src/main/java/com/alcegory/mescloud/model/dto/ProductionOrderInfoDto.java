package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductionOrderInfoDto {

    private long id;
    private String code;
    private Long validAmount;
    private Boolean isCompleted;
    private Date createdAt;
    private Date completedAt;

    private List<ProductionInstructionDto> instructions;
}

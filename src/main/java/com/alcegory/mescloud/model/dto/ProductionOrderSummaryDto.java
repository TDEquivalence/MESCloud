package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductionOrderSummaryDto {

    private long id;
    private CountingEquipmentDto equipment;
    private String code;
    private ImsDto ims;
    private int targetAmount;
    private Date createdAt;
    private Date completedAt;
    private long validAmount;

    private List<ProductionInstructionDto> instructions;
}

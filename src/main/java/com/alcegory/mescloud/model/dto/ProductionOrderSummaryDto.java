package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private long validAmount;
}

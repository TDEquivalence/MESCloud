package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProductionOrderSummaryDto {

    private long id;
    private long equipmentId;
    private String code;
    private String imsCode;
    private int targetAmount;
    private Date createdAt;

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private long validAmount;
}

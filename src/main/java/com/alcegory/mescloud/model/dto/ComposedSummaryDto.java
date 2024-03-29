package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ComposedSummaryDto {

    private Integer id;
    private String code;
    private Date createdAt;
    private Date approvedAt;
    private Date hitInsertedAt;
    private Integer sampleAmount;
    private Double reliability;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private boolean isBatchApproved;
    private String batchCode;
    private Integer amountOfHits;
    private Integer validAmount;
}
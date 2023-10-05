package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ComposedSummaryDto {

    private Integer id;
    private Date createdAt;
    private Integer amount;
    private Integer reliability;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
    private boolean isBatchApproved;
}
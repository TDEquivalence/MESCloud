package com.alcegory.mescloud.model.dto.production;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExportComposedDto {

    private String batchCode;
    private String code;

    private List<ProductionInstructionDto> instructions;

    private Integer validAmount;
    private Integer sampleAmount;

    private Date composedCreatedAt;
    private Integer amountOfHits;
    private Double reliability;
    private Date hitInsertedAt;

    private Boolean status;
    private Date approvedAt;
}
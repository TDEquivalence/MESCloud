package com.alcegory.mescloud.model.dto.composed;

import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ComposedInfoDto {

    private String batchCode;
    private Timestamp createdAt;
    private String code;
    private Integer validAmount;
    private Integer sampleAmount;
    private Integer amountOfHits;
    private Timestamp hitInsertedAt;
    private Double reliability;
    private Boolean isBatchApproved;
    private Timestamp approvedAt;
    private List<ProductionInstructionDto> instructions;
}

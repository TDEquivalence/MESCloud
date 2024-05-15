package com.alcegory.mescloud.model.dto.composed;

import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ComposedInfoDto {
    
    private String code;
    private Timestamp createdAt;
    private Timestamp approvedAt;
    private Timestamp hitInsertedAt;
    private Integer sampleAmount;
    private Double reliability;
    private Boolean isBatchApproved;
    private String batchCode;
    private Integer amountOfHits;
    private Integer validAmount;
    private List<ProductionInstructionDto> instructions;
}

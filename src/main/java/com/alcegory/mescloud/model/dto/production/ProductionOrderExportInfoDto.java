package com.alcegory.mescloud.model.dto.production;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductionOrderExportInfoDto {

    private String equipment;
    private String composedCode;
    private String productionCode;
    private String ims;
    private Long validAmount;
    private Date productionCreatedAt;
    private Date productionCompletedAt;

    private List<ProductionInstructionDto> instructions;
}

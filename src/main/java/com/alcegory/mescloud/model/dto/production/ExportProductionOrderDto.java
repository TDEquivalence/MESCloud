package com.alcegory.mescloud.model.dto.production;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExportProductionOrderDto {

    private String equipment;
    private String composedCode;
    private String code;
    private String ims;

    private List<ProductionInstructionDto> instructionDtos;

    private Long validAmount;
    private Date createdAt;
    private Date completedAt;
}

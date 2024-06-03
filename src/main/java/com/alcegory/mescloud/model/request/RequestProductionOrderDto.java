package com.alcegory.mescloud.model.request;

import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RequestProductionOrderDto {

    private long id;
    private long equipmentId;
    private String imsCode;
    private int targetAmount;
    private Date createdAt;
    private Date completedAt;

    private List<ProductionInstructionDto> instructions;
}

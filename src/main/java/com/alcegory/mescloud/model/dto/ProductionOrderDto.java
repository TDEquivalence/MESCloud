package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.model.entity.ProductionInstructionEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductionOrderDto {

    private long id;
    private long equipmentId;
    private String code;
    private String imsCode;
    private int targetAmount;
    private Date createdAt;
    private Date completedAt;

    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;

    private Map<String, String> productionInstructions;
}

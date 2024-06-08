package com.alcegory.mescloud.model.dto.production;

import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductionOrderDto {

    private long id;
    private String code;
    private int targetAmount;
    private Long validAmount;
    private Boolean isCompleted;
    private Date createdAt;
    private Date completedAt;
    private String composedCode;

    private ImsDto ims;
    private CountingEquipmentDto equipment;
    private List<ProductionInstructionDto> instructions;
}

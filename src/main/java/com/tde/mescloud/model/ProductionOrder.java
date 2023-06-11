package com.tde.mescloud.model;

import com.tde.mescloud.model.entity.ProductionOrderEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProductionOrder {

    private long id;
    private CountingEquipment equipment;
    private String code;
    private int targetAmount;
    //TODO: Implement productionInstruction
    private Date createdAt;

    public ProductionOrder(ProductionOrderEntity entity) {
        this.id = entity.getId();
        CountingEquipment countingEquipment = new CountingEquipment(entity.getEquipment());
        this.equipment = countingEquipment;
        this.code = entity.getCode();
        this.targetAmount = entity.getTargetAmount();
        this.createdAt = entity.getCreatedAt();
    }
}

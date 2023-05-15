package com.tde.mescloud.model;

import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentOutput {

    private long id;
    private CountingEquipment countingEquipment;
    private String code;
    private String alias;

    //TODO: Remove to a converter
    public EquipmentOutput(EquipmentOutputEntity entity) {
        this.id = entity.getId();
        CountingEquipment countingEquipment = new CountingEquipment(entity.getCountingEquipment());
        this.countingEquipment = countingEquipment;
        this.code = entity.getCode();
        this.alias = entity.getAlias().getAlias();
    }
}

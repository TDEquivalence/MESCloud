package com.tde.mescloud.model;

import com.tde.mescloud.model.entity.CountingEquipment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentOutput {

    private CountingEquipment countingEquipment;
    private String code;
    private String alias;
}

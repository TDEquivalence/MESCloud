package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.CountingEquipment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentOutputDto {
    private long id;
    private CountingEquipmentDto countingEquipment;
    private String code;
    private String alias;
}

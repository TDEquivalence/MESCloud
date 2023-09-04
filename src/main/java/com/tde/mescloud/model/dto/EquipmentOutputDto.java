package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentOutputDto {
    private long id;
    private String code;
    private String alias;
    private boolean isValidForProduction;
}

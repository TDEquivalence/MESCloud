package com.tde.mescloud.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentOutput {

    private long id;
    private long countingEquipmentId;
    private String code;
    private String alias;
}

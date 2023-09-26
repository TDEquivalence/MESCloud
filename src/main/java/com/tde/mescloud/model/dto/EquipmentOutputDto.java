package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentOutputDto {
    private long id;
    private String code;
    @JsonUnwrapped
    private EquipmentOutputAliasDto alias;
    @JsonProperty("isValidForProduction")
    private boolean isValidForProduction;
}

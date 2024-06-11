package com.alcegory.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentOutputAliasDto {

    @JsonIgnore
    private Long id;
    private String alias;
}

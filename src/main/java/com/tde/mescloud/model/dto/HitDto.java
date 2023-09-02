package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HitDto {

    private Long id;
    private Long sampleId;
    private Float tca;
    private Boolean isValidForReliability;
}

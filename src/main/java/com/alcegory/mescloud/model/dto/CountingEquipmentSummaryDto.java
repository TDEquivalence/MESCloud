package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountingEquipmentSummaryDto {

    private long id;
    private long sectionId;
    private String code;
    private String alias;
    List<FeatureDto> features;
}
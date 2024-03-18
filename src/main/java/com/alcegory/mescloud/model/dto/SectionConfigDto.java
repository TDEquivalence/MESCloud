package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionConfigDto {

    private Long id;
    private SectionDto section;
    private String name;
    private List<FeatureDto> featureList;
}

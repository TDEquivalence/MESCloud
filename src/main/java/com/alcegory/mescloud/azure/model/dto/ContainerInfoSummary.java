package com.alcegory.mescloud.azure.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerInfoSummary {

    private String path;
    private String sasToken;
    private ImageAnnotationDto imageAnnotationDto;
}

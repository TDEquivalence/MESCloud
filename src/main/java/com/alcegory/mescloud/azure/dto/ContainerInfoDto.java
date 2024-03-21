package com.alcegory.mescloud.azure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerInfoDto {

    private ImageInfoDto jpeg;
    private ImageAnnotationDto imageAnnotationDto;
}

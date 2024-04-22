package com.alcegory.mescloud.azure.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerInfoDto {

    private ImageInfoDto jpg;
    private ImageAnnotationDto imageAnnotationDto;
    private String username;
}

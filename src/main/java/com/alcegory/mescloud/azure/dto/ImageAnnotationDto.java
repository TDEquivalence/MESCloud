package com.alcegory.mescloud.azure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageAnnotationDto {

    private DataDto data;
    private List<ResultDto> resultDtoList;
    private String modelDecision;
    private String userDecision;
}

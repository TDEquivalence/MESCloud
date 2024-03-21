package com.alcegory.mescloud.azure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageAnnotationDto {
    private DataDto data;
    private List<ResultDto> resultDtoList;
    private String modelDecision;
    private String userDecision;
}

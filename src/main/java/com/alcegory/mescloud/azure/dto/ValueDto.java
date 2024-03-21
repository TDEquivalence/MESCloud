package com.alcegory.mescloud.azure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValueDto {

    private Double x;
    private Double y;
    private Double width;
    private Double height;
    private Double rotation;
    private List<String> rectangleLabels;
}

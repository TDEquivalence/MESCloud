package com.alcegory.mescloud.azure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("rectanglelabels")
    private List<String> rectangleLabels;
}

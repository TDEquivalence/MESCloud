package com.alcegory.mescloud.azure.model.dto;

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
    private Integer score;
    @JsonProperty("rectanglelabels")
    private List<String> rectangleLabels;
}

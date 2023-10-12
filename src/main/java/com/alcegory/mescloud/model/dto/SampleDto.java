package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SampleDto {

    private Long id;
    private String composedCode;
    private int amount;
    private double tcaAverage;
    private double reliability;
}

package com.alcegory.mescloud.azure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultDto {

    private String fromName;
    private String toName;
    private String type;
    private Integer originalWidth;
    private Integer originalHeight;
    private ValueDto valueDtoList;
}

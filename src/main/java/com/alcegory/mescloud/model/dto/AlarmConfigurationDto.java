package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmConfigurationDto {

    private Long id;
    private int wordIndex;
    private int bitIndex;
    private String code;
    private String description;
}

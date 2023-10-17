package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmCodeDto {

    private Long id;
    private int word;
    private int index;
    private String code;
    private String description;
}

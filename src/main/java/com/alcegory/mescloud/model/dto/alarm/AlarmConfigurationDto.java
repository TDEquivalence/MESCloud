package com.alcegory.mescloud.model.dto.alarm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmConfigurationDto {

    private Long id;
    private Long equipmentOutputId;
    private int wordIndex;
    private int bitIndex;
    private String code;
    private String description;
}

package com.alcegory.mescloud.model.dto.mqqt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterMqttDto {

    private String outputCode;
    private int value;
}

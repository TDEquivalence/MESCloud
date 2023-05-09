package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterMqttDTO {

    private String outputCode;
    private int value;
}

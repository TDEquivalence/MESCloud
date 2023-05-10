package com.tde.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMqttDTO implements MqttDTO{

    private String jsonType;
}

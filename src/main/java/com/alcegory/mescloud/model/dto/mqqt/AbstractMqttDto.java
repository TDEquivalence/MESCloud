package com.alcegory.mescloud.model.dto.mqqt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMqttDto implements MqttDto {

    private String jsonType;
}

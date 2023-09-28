package com.alcegory.mescloud.protocol;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mes.mqtt")
@Getter
@Setter
public class MesMqttSettings {

    private String protCountBackendTopic;
    private String protCountPlcTopic;
}

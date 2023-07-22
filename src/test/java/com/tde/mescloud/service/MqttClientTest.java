package com.tde.mescloud.service;

import com.tde.mescloud.exception.MesMqttException;
import com.tde.mescloud.api.mqtt.MqttClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class MqttClientTest {

    private final MqttClient mqttClient;

    @Autowired
    public MqttClientTest(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @BeforeEach
    public void setup() {
    }

    @Test
     void subscribe() throws MesMqttException {
        mqttClient.subscribe("Test topic");
        Assertions.assertDoesNotThrow(() -> mqttClient.subscribe("Test topic"));
    }
}

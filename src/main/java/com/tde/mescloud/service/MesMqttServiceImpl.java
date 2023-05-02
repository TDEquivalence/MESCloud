package com.tde.mescloud.service;

import com.tde.mescloud.exception.MesMqttException;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class MesMqttServiceImpl implements MesMqttService {

    private final MqttClient mqttClient;
    private final MesMqttSettings mesMqttSettings;

    public MesMqttServiceImpl(MqttClient mqttClient, MesMqttSettings mesMqttSettings) {
        this.mqttClient = mqttClient;
        this.mesMqttSettings = mesMqttSettings;
    }


    @PostConstruct
    private void initMes() {
        subscribe(mesMqttSettings.getProtCountBackendTopic());
    }

    @Override
    public void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic);
        } catch (MesMqttException e) {
            //TODO: Implement
        }
    }

    @Override
    public void unsubscribe(String topic) {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MesMqttException e) {
            //TODO: Implement
        }
    }

    @Override
    public void publish(String topic, Object payload) {
        try {
            mqttClient.publish(topic, payload);
        } catch (MesMqttException e) {
            //TODO: Implement
        }
    }
}

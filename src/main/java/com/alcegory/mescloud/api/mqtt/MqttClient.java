package com.alcegory.mescloud.api.mqtt;

import com.alcegory.mescloud.exception.MesMqttException;

public interface MqttClient {

    void publish(String topic, Object payload) throws MesMqttException;

    void subscribe(String topic) throws MesMqttException;

    void unsubscribe(String topic) throws MesMqttException;
}
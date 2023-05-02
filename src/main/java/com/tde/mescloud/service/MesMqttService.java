package com.tde.mescloud.service;

public interface MesMqttService {

    void subscribe(String topic);

    void unsubscribe(String topic);

    void publish(String topic, Object payload);
}

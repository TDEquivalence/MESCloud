package com.tde.mescloud.api.mqtt;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import lombok.extern.java.Log;

@Log
public class MqttMessage extends AWSIotMessage {

    public MqttMessage(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }


    @Override
    public void onSuccess() {
        log.info(() -> String.format("Message successfully delivered on topic [%s] with payload [%s]",
                getTopic(), getStringPayload()));
    }

    @Override
    public void onFailure() {
        log.warning(() -> String.format("Failed to deliver message on topic [%s] and QOS [%s], with failure message [%s] and payload [%s]",
                getTopic(), getQos(), getErrorMessage(), getStringPayload()));
    }

    @Override
    public void onTimeout() {
        log.warning(() -> String.format("Message delivery timed out on topic [%s] and QOS [%s] with the payload: [%s]",
                getTopic(), getQos(), getStringPayload()));
    }
}

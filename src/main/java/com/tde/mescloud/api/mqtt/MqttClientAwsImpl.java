package com.tde.mescloud.api.mqtt;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tde.mescloud.exception.MesMqttException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class MqttClientAwsImpl implements MqttClient {

    private final AWSIotMqttClient mqttClient;
    private final MqttClientAwsSettings awsMqttSettings;
    private final ObjectMapper objectMapper;

    public MqttClientAwsImpl(AWSIotMqttClient mqttClient, MqttClientAwsSettings awsSettings, ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.awsMqttSettings = awsSettings;
        this.objectMapper = objectMapper;
    }


    @Override
    public void subscribe(String topic) throws MesMqttException {
        MqttTopic mqttTopic = new MqttTopic(topic, AWSIotQos.QOS0);
        try {
            log.info(() -> String.format("Subscribing topic [%s]", topic));
            mqttClient.subscribe(mqttTopic);
        } catch (AWSIotException e) {
            log.severe(() -> String.format("Exception caught while subscribing the topic [%s] and QOS [%s]",
                    mqttTopic.getTopic(), mqttTopic.getQos()));
            throw new MesMqttException(e);
        }
    }

    @Override
    public void unsubscribe(String topic) throws MesMqttException {
        try {
            log.info(() -> String.format("Unsubscribing topic [%s]", topic));
            mqttClient.unsubscribe(topic);
        } catch (AWSIotException e) {
            log.severe(() -> String.format("Exception caught while subscribing the topic [%s]", topic));
            throw new MesMqttException(e);
        }
    }

    @Override
    public void publish(String topic, Object payload) throws MesMqttException {
        try {
            log.info(() -> String.format("Publishing message on topic [%s]", topic));
            String payloadAsJSON = objectMapper.writeValueAsString(payload);
            MqttMessage mqttMessage = new MqttMessage(topic, AWSIotQos.QOS0, payloadAsJSON);
            mqttClient.publish(mqttMessage, awsMqttSettings.getConnectionTimeout());
        } catch (AWSIotException e) {
            log.severe(() -> String.format("Exception caught while publishing to [%s] with QOS [%s]",
                    topic, AWSIotQos.QOS0));
            throw new MesMqttException(e);
        } catch (JsonProcessingException e) {
            log.severe("Exception caught while serializing the payload object");
            throw new MesMqttException(e);
        }
    }

    public void connectToBroker() throws AWSIotException {
        if(isDisconnected()) {
            log.warning("Connecting to broker.");
            awsMqttSettings.getAwsIotMqttCLient().connect();
        }
    }

    private boolean isDisconnected() {
        return !AWSIotConnectionStatus.CONNECTED.equals(awsMqttSettings.getAwsIotMqttCLient().getConnectionStatus());
    }
}

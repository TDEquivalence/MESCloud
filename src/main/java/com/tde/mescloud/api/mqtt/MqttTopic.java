package com.tde.mescloud.api.mqtt;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.tde.mescloud.protocol.CountProtocol;
import com.tde.mescloud.utility.SpringContext;
import lombok.extern.java.Log;

@Log
public class MqttTopic extends AWSIotTopic {

    public MqttTopic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }


    @Override
    public void onMessage(AWSIotMessage message) {
        try {
            log.info(() -> String.format("Message received on topic [%s]", message.getTopic()));
            SpringContext.getBean(CountProtocol.class, CountProtocol.BEAN_NAME).react(message);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
}

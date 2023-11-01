package com.alcegory.mescloud.api.mqtt;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.alcegory.mescloud.protocol.CountProtocol;
import com.alcegory.mescloud.utility.SpringContext;
import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.logging.Level;

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
            log.log(Level.SEVERE, e, () -> e.getMessage());
            log.severe(Arrays.toString(e.getStackTrace()));
        }
    }
}

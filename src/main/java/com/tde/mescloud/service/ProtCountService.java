package com.tde.mescloud.service;

import com.amazonaws.services.iot.client.AWSIotMessage;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static com.tde.mescloud.service.ProtCountService.BEAN_NAME;

@Component(BEAN_NAME)
@Log
public class ProtCountService implements MesProtocol {

    public final static String BEAN_NAME = "protCountService";
    private final MesMqttService mesMqttService;
    private final MesMqttSettings mesMqttSettings;

    public ProtCountService (MesMqttService mesMqttService, MesMqttSettings mesMqttSettings) {
        this.mesMqttService = mesMqttService;
        this.mesMqttSettings = mesMqttSettings;
    }

    @Override
    public void react(AWSIotMessage message) {
        log.info(() -> String.format("Message delegated to [%s] on topic [%s] with payload: [%s]",
                this.getClass().toString(), message.getTopic(), message.getStringPayload()));
        publishHasReceived(message);
        getMesMqttDto();
        processMessage();
    }

    private void publishHasReceived(AWSIotMessage message) {
        //TODO: Implement
    }

    private void getMesMqttDto() {
        //TODO: Implement
    }

    private void processMessage() {
        //TODO: Implement
    }
}
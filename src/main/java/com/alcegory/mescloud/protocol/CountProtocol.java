package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.model.dto.mqqt.HasReceivedMqttDto;
import com.alcegory.mescloud.model.dto.mqqt.MqttDto;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD
import com.alcegory.mescloud.model.dto.mqqt.HasReceivedMqttDto;
import com.alcegory.mescloud.model.dto.mqqt.MqttDto;
=======
>>>>>>> test_environment
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Component(CountProtocol.BEAN_NAME)
@Log
public class CountProtocol extends AbstractMesProtocol {

    public static final String BEAN_NAME = "protCountService";

    private final ObjectMapper objectMapper;
    private final MqttClient mqttClient;
    private final MesMqttSettings mesMqttSettings;

    public CountProtocol(ObjectMapper objectMapper, MqttClient mqttClient, MesMqttSettings mesMqttSettings) {
        this.objectMapper = objectMapper;
        this.mqttClient = mqttClient;
        this.mesMqttSettings = mesMqttSettings;
    }


    @PostConstruct
    private void subscribeProtocolTopics() throws MesMqttException {
        List<String> backendTopics = mesMqttSettings.getAllBackendTopics();
        for (String topic : backendTopics) {
            try {
                mqttClient.subscribe(topic);
            } catch (MesMqttException e) {
                log.log(Level.SEVERE, e, () -> String.format("Unable to subscribe topic [%s]", topic));
                throw new MesMqttException(String.format("Unable to subscribe topic [%s]", topic));
            }
        }
    }

    @Override
    public void react(AWSIotMessage message) {

        log.info(() -> String.format("Message delegated to [%s] on topic [%s] with payload: [%s]",
                this.getClass(), message.getTopic(), message.getStringPayload()));

        Optional<MqttDto> optMqttDTO = parseMqttDTO(message);
        if (optMqttDTO.isEmpty()) {
            log.warning("An error occurred while parsing the MQTT message");
            return;
        }

        MqttDto mqttDTO = optMqttDTO.get();
        publishHasReceived(mqttDTO, message);
        executeMesProcess(mqttDTO, message);
    }

    private Optional<MqttDto> parseMqttDTO(AWSIotMessage message) {
        try {
            MqttDto mqttDTO = objectMapper.readValue(message.getStringPayload(), MqttDto.class);
            return Optional.of(mqttDTO);
        } catch (JsonMappingException e) {
            log.log(Level.SEVERE, e, () -> String.format("Unable to map JSON [%s]", message.getStringPayload()));
            return Optional.empty();
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE, e, () -> String.format("Unable to deserialize JSON [%s]", message.getStringPayload()));
            return Optional.empty();
        }
    }

    private void publishHasReceived(MqttDto mqttDTO, AWSIotMessage message) {
        try {
            HasReceivedMqttDto hasReceivedMqttDTO = new HasReceivedMqttDto(mqttDTO.getEquipmentCode());
            mqttClient.publish(mesMqttSettings.getOppositeTopic(message.getTopic()), hasReceivedMqttDTO);
        } catch (MesMqttException e) {
            log.log(Level.SEVERE, e, () -> String.format("Failed to publish Has Received message as a response to [%s] for equipment [%s]",
                    mqttDTO.getJsonType(), mqttDTO.getEquipmentCode()));
            throw new RuntimeException(e);
        }
    }
}
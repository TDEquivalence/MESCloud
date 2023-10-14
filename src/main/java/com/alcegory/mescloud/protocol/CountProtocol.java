package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.utility.LockUtil;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alcegory.mescloud.model.dto.HasReceivedMqttDto;
import com.alcegory.mescloud.model.dto.MqttDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Level;

@Component(CountProtocol.BEAN_NAME)
@Log
public class CountProtocol extends AbstractMesProtocol {

    private final String EMPTY_PRODUCTION_ORDER = "";
    public static final String BEAN_NAME = "protCountService";

    private final ObjectMapper objectMapper;
    private final MqttClient mqttClient;
    private final MesMqttSettings mesMqttSettings;
    private final LockUtil lockHandler;

    public CountProtocol(ObjectMapper objectMapper, MqttClient mqttClient, MesMqttSettings mesMqttSettings, LockUtil lockHandler) {
        this.objectMapper = objectMapper;
        this.mqttClient = mqttClient;
        this.mesMqttSettings = mesMqttSettings;
        this.lockHandler = lockHandler;
    }


    @PostConstruct
    private void subscribeProtocolTopic() throws MesMqttException {
        try {
            mqttClient.subscribe(mesMqttSettings.getProtCountBackendTopic());
        } catch (MesMqttException e) {
            log.log(Level.SEVERE, e, () -> String.format("Unable to subscribe topic [%s] on [%s]", mesMqttSettings.getProtCountBackendTopic(),
                    this.getClass().getName()));
            throw new MesMqttException(String.format("Unable to subscribe topic [%s] on [%s]",
                    mesMqttSettings.getProtCountBackendTopic(), this.getClass().getName()));
        }
    }

    @Override
    public void react(AWSIotMessage message) {

        log.info(() -> String.format("Message delegated to [%s] on topic [%s] with payload: [%s]",
                this.getClass().toString(), message.getTopic(), message.getStringPayload()));

        Optional<MqttDto> optMqttDTO = parseMqttDTO(message);
        if (optMqttDTO.isEmpty()) {
            log.warning("An error occurred while parsing the MQTT message");
            return;
        }

        MqttDto mqttDTO = optMqttDTO.get();
        publishHasReceived(mqttDTO);
        executeMesProcess(mqttDTO);
        if (isToConcludeProductionOrder(mqttDTO)) {
            String equipmentCode = mqttDTO.getEquipmentCode(); // Assuming it returns a String
            lockHandler.unlock(equipmentCode);
        }
    }

    private boolean isToConcludeProductionOrder(MqttDto mqttDTO) {
        if (mqttDTO != null && MqttDTOConstants.COUNTING_RECORD_DTO_NAME.equals(mqttDTO.getJsonType())) {
            PlcMqttDto plcMqttDto = (PlcMqttDto) mqttDTO;
            return EMPTY_PRODUCTION_ORDER.equals(plcMqttDto.getProductionOrderCode()) && plcMqttDto.getEquipmentStatus() == 0;
        }
        return false;
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
    
    private void publishHasReceived(MqttDto mqttDTO) {
        try {
            HasReceivedMqttDto hasReceivedMqttDTO = new HasReceivedMqttDto(mqttDTO.getEquipmentCode());
            mqttClient.publish(mesMqttSettings.getProtCountPlcTopic(), hasReceivedMqttDTO);
        } catch (MesMqttException e) {
            log.log(Level.SEVERE, e, () -> String.format("Failed to publish Has Received message as a response to [%s] for equipment [%s]",
                    mqttDTO.getJsonType(), mqttDTO.getEquipmentCode()));
            throw new RuntimeException(e);
        }
    }
}
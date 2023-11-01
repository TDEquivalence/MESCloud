package com.alcegory.mescloud.api.mqtt;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.CredentialsProvider;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.mqtt")
@Getter
@Setter
@Log
public class MqttClientAwsSettings {

    private AWSIotMqttClient mqttClient;
    private static final long CONNECTION_TIMEOUT = 3000;
    private String clientEndpoint;
    private String clientId;
    private String awsAccessKeyId;
    private String awsSecretAccessKey;
    private String awsRegion;


    @Bean
    public AWSIotMqttClient getAwsIotMqttCLient() {
        if(mqttClient == null) {
            createMqttClient();
        }
        return mqttClient;
    }

    private void createMqttClient() {
        try {
            Credentials credentials = new Credentials(awsAccessKeyId, awsSecretAccessKey);
            CredentialsProvider credentialsProvider = new StaticCredentialsProvider(credentials);
            mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, credentialsProvider, awsRegion);
            mqttClient.connect();
        } catch (AWSIotException e) {
            log.severe(() -> String.format("Exception instantiating a MQTT Client for [%s] at the endpoint [%s]",
                    clientId, clientEndpoint));
            throw new RuntimeException(e);
        }
    }
}

package com.tde.mescloud.api.rest;

import com.tde.mescloud.api.mqtt.MqttClient;
import com.tde.mescloud.exception.MesMqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for MQTT testing purposes
 */
@RestController
@RequestMapping("api/mqtt")
public class MqttController {

    private final MqttClient mqttClient;

    public MqttController(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @GetMapping
    public ResponseEntity<String> controllerTest() {
        return new ResponseEntity<>("REST controller on.", HttpStatus.OK);
    }

    //TODO: Create a DTO that holds both the topic & payload
    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestParam(name = "topic") String topic,
                                          @RequestBody Object payload) {
        try {
            mqttClient.publish(topic, payload);
            return new ResponseEntity<>("Message published successfully", HttpStatus.OK);
        } catch (MesMqttException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody String topic) {
        try {
            mqttClient.subscribe(topic);
            return new ResponseEntity<>("Subscribed topic: " + topic, HttpStatus.OK);
        } catch (MesMqttException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestBody String topic) {
        try {
            mqttClient.unsubscribe(topic);
            return new ResponseEntity<>("Topic unsubscribed successfully", HttpStatus.OK);
        } catch (MesMqttException e) {
            throw new RuntimeException(e);
        }
    }
}

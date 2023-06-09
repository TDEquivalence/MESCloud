package com.tde.mescloud.api.rest;

import com.tde.mescloud.api.mqtt.MqttClient;
import com.tde.mescloud.exception.MesMqttException;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for MQTT testing purposes
 */
@RestController
@RequestMapping("api/mqtt")
@Log
public class MqttController {

    private final MqttClient mqttClient;

    public MqttController(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @GetMapping
    public ResponseEntity<String> controllerTest() {
        return new ResponseEntity<>("REST controller on.", HttpStatus.OK);
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestParam(name = "topic") String topic,
                                          @RequestBody Object payload) {
        try {
            mqttClient.publish(topic, payload);
            return new ResponseEntity<>("Message published successfully", HttpStatus.OK);
        } catch (MesMqttException e) {
            String msg = String.format("Unable to publish on topic [%s] with the payload: [%s]", topic, payload.toString());
            log.severe(msg);
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody String topic) {
        try {
            mqttClient.subscribe(topic);
            return new ResponseEntity<>("Subscribed topic: " + topic, HttpStatus.OK);
        } catch (MesMqttException e) {
            String msg = String.format("Unable to subscribe topic [%s]", topic);
            log.severe(msg);
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestBody String topic) {
        try {
            mqttClient.unsubscribe(topic);
            return new ResponseEntity<>("Topic unsubscribed successfully", HttpStatus.OK);
        } catch (MesMqttException e) {
            String msg = String.format("Unable to unsubscribe topic [%s]", topic);
            log.severe(msg);
            return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

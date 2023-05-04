package com.tde.mescloud.controller;

import com.tde.mescloud.service.MesMqttService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for MQTT testing purposes
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {

    private final MesMqttService mesMqttService;

    public MqttController(MesMqttService mesMqttService) {
        this.mesMqttService = mesMqttService;
    }

    @GetMapping
    public ResponseEntity<String> controllerTest() {
        return new ResponseEntity<>("REST controller on.", HttpStatus.OK);
    }

    //TODO: Create a DTO that holds both the topic & payload
    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestParam(name = "topic") String topic,
                                          @RequestBody Object payload) {
        mesMqttService.publish(topic, payload);
        return new ResponseEntity<>("Message published successfully", HttpStatus.OK);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody String topic) {
        mesMqttService.subscribe(topic);
        return new ResponseEntity<>("Subscribed topic: " + topic, HttpStatus.OK);
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestBody String topic) {
        mesMqttService.unsubscribe(topic);
        return new ResponseEntity<>("Topic unsubscribed successfully", HttpStatus.OK);
    }
}

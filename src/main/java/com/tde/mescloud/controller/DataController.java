package com.tde.mescloud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DataController {

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("YEAH", HttpStatus.OK);
    }

    @GetMapping("/version")
    public String version() {
        return "Version MES 0.0.1";
    }

    @GetMapping("/string")
    public ResponseEntity<String> getRandomNations() {
        return new ResponseEntity<>("Loving DevOps... Or not!", HttpStatus.OK);
    }
}

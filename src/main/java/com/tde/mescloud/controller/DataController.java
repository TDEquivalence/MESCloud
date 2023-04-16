package com.tde.mescloud.controller;

import com.tde.mescloud.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tde.mescloud.Constants.CELEBRATION_TIME;

@RestController
@RequestMapping("/")
public class DataController {

    private Constants constants;

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>(CELEBRATION_TIME, HttpStatus.OK);
    }

    @GetMapping("/version")
    public String version() {
        return constants.getVersion();
    }

    @GetMapping("/string")
    public ResponseEntity<String> getRandomNations() {
        return new ResponseEntity<>("Loving DevOps... Or not!", HttpStatus.OK);
    }
}

package com.tde.mescloud;

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
        return new ResponseEntity<>("FONIXXXXXX estava a ver que não", HttpStatus.OK);
    }

    @GetMapping("/version")
    public String version() {
        return "The actual version is 1.0.0 MES";
    }

    @GetMapping("/string")
    public ResponseEntity<String> getRandomNations() {
        String stringToReturn = "Hey world and scenes";
        return new ResponseEntity<>(stringToReturn, HttpStatus.OK);
    }
}

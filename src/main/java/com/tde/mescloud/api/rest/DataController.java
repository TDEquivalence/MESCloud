package com.tde.mescloud.api.rest;

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
        return new ResponseEntity<>("We are Alive! APRIL 26", HttpStatus.OK);
    }

    @GetMapping("aws/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("HEALTH PATH! APRIL 26! WE ARE DYING HERE! COME ON AWS!!!", HttpStatus.OK);
    }
}

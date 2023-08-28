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
        return new ResponseEntity<>("We are Alive! AUGUST 28: Just one small positive thought in the morning can change your whole day.", HttpStatus.OK);
    }

    @GetMapping("aws/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("HEALTH PATH! AUGUST 28: \"There is nothing impossible to they who will try.\"\n" +
                "— Alexander the Great!!!", HttpStatus.OK);
    }
}

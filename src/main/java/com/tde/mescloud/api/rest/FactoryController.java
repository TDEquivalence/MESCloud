package com.tde.mescloud.api.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/factory")
@AllArgsConstructor
public class FactoryController {


    @GetMapping("/save")
    public ResponseEntity<String> save() {

    }
}

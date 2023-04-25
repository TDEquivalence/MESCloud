package com.tde.mescloud.controller;

import com.tde.mescloud.model.FactoryEntity;
import com.tde.mescloud.repository.FactoryRepository;
import org.aspectj.runtime.reflect.Factory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/factory")
public class FactoryController {

    private FactoryRepository factoryRepository;

    public FactoryController(FactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @GetMapping("/save")
    public ResponseEntity<String> save() {
        FactoryEntity factoryEntity = new FactoryEntity();
        factoryEntity.setName("testFactory");

        factoryRepository.save(factoryEntity);

        return new ResponseEntity<>("Saved", HttpStatus.OK);
    }
}

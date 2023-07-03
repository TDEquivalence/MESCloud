package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.FactoryDto;
import com.tde.mescloud.service.FactoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/factory")
@AllArgsConstructor
public class FactoryController {

    private final FactoryService factoryService;

    @GetMapping("/{id}")
    public ResponseEntity<FactoryDto> getFactoryById(@PathVariable("id") Long id) {
        FactoryDto factoryDto = factoryService.getFactoryById(id);
        if (factoryDto != null) {
            return ResponseEntity.ok(factoryDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<FactoryDto> saveFactory(@RequestBody FactoryDto factoryDto) {
        FactoryDto savedFactoryDto = factoryService.saveFactory(factoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFactoryDto);
    }
}

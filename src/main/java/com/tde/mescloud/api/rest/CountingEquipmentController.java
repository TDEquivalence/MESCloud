package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.service.CountingEquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/counting-equipments")
@AllArgsConstructor
public class CountingEquipmentController {

    private CountingEquipmentService service;

    @GetMapping
    public ResponseEntity<List<CountingEquipmentDto>> findAll() {
        List<CountingEquipmentDto> countingEquipments = service.findAllWithLastProductionOrder();
        return new ResponseEntity<>(countingEquipments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingEquipmentDto> findById(@PathVariable long id) {
        Optional<CountingEquipmentDto> countingEquipmentOpt = service.findById(id);
        if (countingEquipmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(countingEquipmentOpt.get(), HttpStatus.OK);
    }

    @PutMapping("/{equipmentId}/ims")
    public ResponseEntity<CountingEquipmentDto> updateIms(@PathVariable long equipmentId, @RequestBody Long imsId) {
        Optional<CountingEquipmentDto> updatedIms = service.updateIms(equipmentId, imsId);
        if (updatedIms.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedIms.get(), HttpStatus.OK);
    }
}

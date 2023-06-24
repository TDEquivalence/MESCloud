package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.CountingEquipmentDto;
import com.tde.mescloud.service.CountingEquipmentService;
import com.tde.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/counting-equipments")
@AllArgsConstructor
public class CountingEquipmentController {

    private CountingEquipmentService countingEquipmentService;
    private ProductionOrderService productionOrderService;

    @GetMapping
    public ResponseEntity<List<CountingEquipmentDto>> findAll() {
        List<CountingEquipmentDto> countingEquipments = countingEquipmentService.findAll();
        return new ResponseEntity<>(countingEquipments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountingEquipmentDto> findById(@PathVariable long id) {
        Optional<CountingEquipmentDto> countingEquipmentOpt = countingEquipmentService.findById(id);
        if (countingEquipmentOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CountingEquipmentDto countingEquipment = countingEquipmentOpt.get();
        //TODO: This can be removed using Projections. Consider changing the entity...
        boolean hasActiveProductionOrder = productionOrderService.hasActiveProductionOrder(countingEquipment.getId());
        countingEquipment.setHasActiveProductionOrder(hasActiveProductionOrder);

        return new ResponseEntity<>(countingEquipment, HttpStatus.OK);
    }
}

package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/production-orders")
@AllArgsConstructor
public class ProductionOrderController {

    private ProductionOrderService productionOrderService;


    @PostMapping
    public ResponseEntity<ProductionOrderDto> create(@RequestBody ProductionOrderDto requestProductionOrder) {
        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.create(requestProductionOrder);
        if (productionOrderOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }

    //TODO: Change to PutMapping
    @PostMapping("{countingEquipmentId}/complete")
    public ResponseEntity<ProductionOrderDto> complete(@PathVariable long countingEquipmentId) {
        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.complete(countingEquipmentId);
        if (productionOrderOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }
}
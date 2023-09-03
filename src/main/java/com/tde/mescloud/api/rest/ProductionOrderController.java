package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.ProductionOrderDto;
import com.tde.mescloud.model.dto.ProductionOrderSummaryDto;
import com.tde.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/production-orders")
@AllArgsConstructor
public class ProductionOrderController {

    private ProductionOrderService service;


    @PostMapping
    public ResponseEntity<ProductionOrderDto> create(@RequestBody ProductionOrderDto requestProductionOrder) {
        Optional<ProductionOrderDto> productionOrderOpt = service.create(requestProductionOrder);
        if (productionOrderOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }

    //TODO: Change to PutMapping
    @PostMapping("{countingEquipmentId}/complete")
    public ResponseEntity<ProductionOrderDto> complete(@PathVariable long countingEquipmentId) {
        Optional<ProductionOrderDto> productionOrderOpt = service.complete(countingEquipmentId);
        if (productionOrderOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<ProductionOrderSummaryDto>> getCompleted() {
        List<ProductionOrderSummaryDto> completedOrders = service.getCompleted();
        return new ResponseEntity<>(completedOrders, HttpStatus.OK);
    }
}
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
    public ResponseEntity<ProductionOrderDto> save(@RequestBody ProductionOrderDto requestProductionOrder) {
        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.save(requestProductionOrder);
        return handleResponse(productionOrderOpt);
    }

    @PostMapping("{countingEquipmentId}/complete")
    public ResponseEntity<ProductionOrderDto> complete(@PathVariable long countingEquipmentId) {
        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.complete(countingEquipmentId);
        return handleResponse(productionOrderOpt);
    }

    private ResponseEntity<ProductionOrderDto> handleResponse(Optional<ProductionOrderDto> productionOrderOpt) {
        return productionOrderOpt.isEmpty() ?
                new ResponseEntity<>(HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }
}
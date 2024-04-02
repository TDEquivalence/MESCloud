package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.ProductionOrderService;
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

    private final ProductionOrderService service;

    @PostMapping
    public ResponseEntity<ProductionOrderDto> create(@RequestBody ProductionOrderDto requestProductionOrder) {
        Optional<ProductionOrderDto> productionOrderOpt = service.create(requestProductionOrder);
        if (productionOrderOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<ProductionOrderDto> edit(@RequestBody ProductionOrderDto requestProductionOrder) {
        Optional<ProductionOrderDto> productionOrder = service.editProductionOrder(requestProductionOrder);
        if (productionOrder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(productionOrder.get(), HttpStatus.OK);
    }

    @PutMapping("{countingEquipmentId}/complete")
    public ResponseEntity<ProductionOrderDto> complete(@PathVariable long countingEquipmentId) {
        Optional<ProductionOrderDto> productionOrderOpt = service.complete(countingEquipmentId);
        if (productionOrderOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<ProductionOrderSummaryDto>> getAllCompleted() {
        List<ProductionOrderSummaryDto> completedOrders = service.getCompletedWithoutComposedFiltered();
        return new ResponseEntity<>(completedOrders, HttpStatus.OK);
    }

    @PostMapping("/completed/filtered")
    public ResponseEntity<PaginatedProductionOrderDto> getCompletedFiltered(@RequestBody Filter filter) {
        PaginatedProductionOrderDto completedOrders = service.getCompletedWithoutComposedFiltered(filter);
        return new ResponseEntity<>(completedOrders, HttpStatus.OK);
    }
}
package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.dto.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.ProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/production-orders")
@AllArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService service;

    @PostMapping
    public ResponseEntity<ProductionOrderDto> create(@RequestBody ProductionOrderDto requestProductionOrder,
                                                     Authentication authentication) {

        try {
            Optional<ProductionOrderDto> productionOrderOpt = service.create(requestProductionOrder, authentication);
            if (productionOrderOpt.isPresent()) {
                return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (ForbiddenAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<ProductionOrderDto> edit(@RequestBody ProductionOrderDto requestProductionOrder,
                                                   Authentication authentication) {
        try {
            Optional<ProductionOrderDto> editedProductionOrder = service.editProductionOrder(requestProductionOrder, authentication);

            if (editedProductionOrder.isPresent()) {
                return ResponseEntity.ok(editedProductionOrder.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("{countingEquipmentId}/complete")
    public ResponseEntity<ProductionOrderDto> complete(@PathVariable long countingEquipmentId, Authentication authentication) {
        try {
            Optional<ProductionOrderDto> productionOrderOpt = service.complete(countingEquipmentId, authentication);
            if (productionOrderOpt.isPresent()) {
                return ResponseEntity.ok(productionOrderOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
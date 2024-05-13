package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.dto.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;
import com.alcegory.mescloud.service.management.ManagementInfoService;
import com.alcegory.mescloud.service.management.ProductionOrderManagementService;
import com.alcegory.mescloud.service.production.ProductionOrderService;
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
    private final ProductionOrderManagementService productionOrderManagementService;
    private final ManagementInfoService managementInfoService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderDto> getProductionOrderById(@PathVariable Long id) {
        try {
            Optional<ProductionOrderDto> productionOrderOpt = service.getProductionOrderById(id);
            if (productionOrderOpt.isPresent()) {
                return ResponseEntity.ok(productionOrderOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductionOrderDto> create(@RequestBody RequestProductionOrderDto requestProductionOrder,
                                                     Authentication authentication) {

        try {
            Optional<ProductionOrderDto> productionOrderOpt = productionOrderManagementService.create(requestProductionOrder, authentication);
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
            ProductionOrderDto editedProductionOrder = productionOrderManagementService.editProductionOrder(requestProductionOrder, authentication);

            if (editedProductionOrder != null) {
                return ResponseEntity.ok(editedProductionOrder);
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
            Optional<ProductionOrderDto> productionOrderOpt
                    = productionOrderManagementService.complete(countingEquipmentId, authentication);
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
    public ResponseEntity<List<ProductionOrderDto>> getAllCompleted() {
        try {
            List<ProductionOrderDto> completedOrders = service.getCompletedWithoutComposedFiltered();
            return ResponseEntity.ok(completedOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/completed/filtered")
    public ResponseEntity<PaginatedProductionOrderDto> getCompletedFiltered(@RequestBody Filter filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            PaginatedProductionOrderDto completedOrders = managementInfoService.getCompletedWithoutComposedFiltered(filter);
            return ResponseEntity.ok(completedOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
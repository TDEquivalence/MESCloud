package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.dto.pagination.PaginatedProductionOrderDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
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
@AllArgsConstructor
public class ProductionOrderController extends SectionBaseController {

    private static final String PRODUCTION_ORDERS = "/production-orders";

    private final ProductionOrderService service;
    private final ProductionOrderManagementService productionOrderManagementService;
    private final ManagementInfoService managementInfoService;

    @GetMapping(PRODUCTION_ORDERS + "/{id}")
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

    @PostMapping(PRODUCTION_ORDERS)
    public ResponseEntity<ProductionOrderDto> create(@PathVariable String companyPrefix, @PathVariable String sectionPrefix,
                                                     @PathVariable long sectionId, @RequestBody RequestProductionOrderDto requestProductionOrder,
                                                     Authentication authentication) {

        try {
            Optional<ProductionOrderDto> productionOrderOpt = productionOrderManagementService.create(companyPrefix, sectionPrefix,
                    sectionId, requestProductionOrder, authentication);
            if (productionOrderOpt.isPresent()) {
                return new ResponseEntity<>(productionOrderOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (ForbiddenAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(PRODUCTION_ORDERS + "/edit")
    public ResponseEntity<ProductionOrderDto> edit(@PathVariable long sectionId, @RequestBody ProductionOrderDto requestProductionOrder,
                                                   Authentication authentication) {

        try {
            ProductionOrderDto editedProductionOrder = productionOrderManagementService.editProductionOrder(requestProductionOrder,
                    authentication, sectionId);

            if (editedProductionOrder != null) {
                return ResponseEntity.ok(editedProductionOrder);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(PRODUCTION_ORDERS + "/{countingEquipmentId}/complete")
    public ResponseEntity<ProductionOrderDto> complete(@PathVariable String companyPrefix, @PathVariable String sectionPrefix,
                                                       @PathVariable long sectionId, @PathVariable long countingEquipmentId,
                                                       Authentication authentication) {
        try {
            Optional<ProductionOrderDto> productionOrderOpt
                    = productionOrderManagementService.complete(companyPrefix, sectionPrefix, sectionId,
                    countingEquipmentId, authentication);
            if (productionOrderOpt.isPresent()) {
                return ResponseEntity.ok(productionOrderOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping(PRODUCTION_ORDERS + "/completed")
    public ResponseEntity<List<ProductionOrderDto>> getAllCompleted(@PathVariable long sectionId) {
        try {
            List<ProductionOrderDto> completedOrders = service.getCompletedWithoutComposedFiltered(sectionId);
            return ResponseEntity.ok(completedOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(PRODUCTION_ORDERS + "/completed/filtered")
    public ResponseEntity<PaginatedProductionOrderDto> getCompletedFiltered(@PathVariable long sectionId, @RequestBody Filter filter) {
        try {
            if (filter == null) {
                return ResponseEntity.badRequest().build();
            }

            PaginatedProductionOrderDto completedOrders = managementInfoService.getCompletedWithoutComposedFiltered(sectionId,
                    filter);
            return ResponseEntity.ok(completedOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
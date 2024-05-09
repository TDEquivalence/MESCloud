package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ComposedSummaryDto;
import com.alcegory.mescloud.model.dto.PaginatedComposedDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestComposedDto;
import com.alcegory.mescloud.service.ComposedProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/composed-production-order")
@AllArgsConstructor
public class ComposedProductionOrderController {

    private final ComposedProductionOrderService composedService;

    @PostMapping
    public ResponseEntity<ComposedProductionOrderDto> create(@RequestBody RequestComposedDto productionOrderIds) {
        try {
            Optional<ComposedProductionOrderDto> composedProductionOpt = composedService.create(productionOrderIds);
            if (composedProductionOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(composedProductionOpt.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ComposedProductionOrderDto>> findAll() {
        try {
            List<ComposedProductionOrderDto> composedDtos = composedService.getAll();
            if (composedDtos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/insert-hits")
    public ResponseEntity<PaginatedComposedDto> findAllToInsertHits() {
        try {
            PaginatedComposedDto composedWithoutHits = composedService.findAllSummarizedWithoutHits();
            if (composedWithoutHits == null || composedWithoutHits.getComposedProductionOrders().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insert-hits/filtered")
    public ResponseEntity<PaginatedComposedDto> findToInsertHitsFiltered(@RequestBody Filter filter) {
        try {
            PaginatedComposedDto composedWithoutHits = composedService.findSummarizedWithoutHitsFiltered(filter);
            if (composedWithoutHits == null || composedWithoutHits.getComposedProductionOrders().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/approval")
    public ResponseEntity<PaginatedComposedDto> findAllForApproval() {
        try {
            PaginatedComposedDto composedWithHits = composedService.findAllSummarizedWithHits();
            if (composedWithHits == null || composedWithHits.getComposedProductionOrders().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/approval/filtered")
    public ResponseEntity<PaginatedComposedDto> findForApprovalFiltered(@RequestBody Filter filter) {
        try {
            PaginatedComposedDto composedWithHits = composedService.findSummarizedWithHitsFiltered(filter);
            if (composedWithHits == null || composedWithHits.getComposedProductionOrders().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/completed")
    public ResponseEntity<List<ComposedSummaryDto>> findAllCompleted() {
        try {
            List<ComposedSummaryDto> composedCompleted = composedService.findAllCompleted();
            if (composedCompleted.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/completed/filtered")
    public ResponseEntity<PaginatedComposedDto> findCompletedFiltered(@RequestBody Filter filter) {
        try {
            PaginatedComposedDto composedCompleted = composedService.findCompletedFiltered(filter);
            if (composedCompleted == null || composedCompleted.getComposedProductionOrders().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/production-orders")
    public ResponseEntity<List<ProductionOrderDto>> getProductionOrderSummaryByComposedId(@RequestBody RequestById request) {
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            List<ProductionOrderDto> productionOrderSummary =
                    composedService.getProductionOrderSummaryByComposedId(request.getId());
            return new ResponseEntity<>(productionOrderSummary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
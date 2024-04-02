package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.ComposedSummaryDto;
import com.alcegory.mescloud.model.dto.PaginatedComposedDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
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
        Optional<ComposedProductionOrderDto> composedProductionOpt = composedService.create(productionOrderIds);
        if (composedProductionOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(composedProductionOpt.get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ComposedProductionOrderDto>> findAll() {
        List<ComposedProductionOrderDto> composedDtos = composedService.getAll();
        return new ResponseEntity<>(composedDtos, HttpStatus.OK);
    }

    @GetMapping("/insert-hits")
    public ResponseEntity<PaginatedComposedDto> findAllToInsertHits() {
        PaginatedComposedDto composedWithoutHits = composedService.findAllSummarizedWithoutHits();
        return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
    }

    @PostMapping("/insert-hits/filtered")
    public ResponseEntity<PaginatedComposedDto> findToInsertHitsFiltered(@RequestBody Filter filter) {
        PaginatedComposedDto composedWithoutHits = composedService.findSummarizedWithoutHitsFiltered(filter);
        return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
    }

    @GetMapping("/approval")
    public ResponseEntity<PaginatedComposedDto> findAllForApproval() {
        PaginatedComposedDto composedWithoutHits = composedService.findAllSummarizedWithHits();
        return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
    }

    @PostMapping("/approval/filtered")
    public ResponseEntity<PaginatedComposedDto> findForApprovalFiltered(@RequestBody Filter filter) {
        PaginatedComposedDto composedWithoutHits = composedService.findSummarizedWithHitsFiltered(filter);
        return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<ComposedSummaryDto>> findAllCompleted() {
        List<ComposedSummaryDto> composedCompleted = composedService.findAllCompleted();
        return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
    }

    @PostMapping("/completed/filtered")
    public ResponseEntity<PaginatedComposedDto> findCompletedFiltered(@RequestBody Filter filter) {
        PaginatedComposedDto composedCompleted = composedService.findCompletedFiltered(filter);
        return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
    }

    @PostMapping("/production-orders")
    public ResponseEntity<List<ProductionOrderSummaryDto>> getProductionOrderSummaryByComposedId(@RequestBody RequestById request) {
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            List<ProductionOrderSummaryDto> productionOrderSummary = composedService.getProductionOrderSummaryByComposedId(request.getId());
            return new ResponseEntity<>(productionOrderSummary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
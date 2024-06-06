package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.model.dto.composed.ComposedProductionOrderDto;
import com.alcegory.mescloud.model.dto.composed.ComposedSummaryDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedComposedDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestComposedDto;
import com.alcegory.mescloud.service.composed.ComposedProductionOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class ComposedProductionOrderController extends SectionBaseController {

    private static final String COMPOSED_PRODUCTION_ORDER_URL = "/composed-production-order";

    private final ComposedProductionOrderService composedService;

    @PostMapping(COMPOSED_PRODUCTION_ORDER_URL)
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

    @GetMapping(COMPOSED_PRODUCTION_ORDER_URL)
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

    @GetMapping(COMPOSED_PRODUCTION_ORDER_URL + "/insert-hits")
    public ResponseEntity<PaginatedComposedDto> findAllToInsertHits(@PathVariable long sectionId) {
        try {
            PaginatedComposedDto composedWithoutHits = composedService.findAllSummarizedWithoutHits(sectionId);
            if (composedWithoutHits == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(COMPOSED_PRODUCTION_ORDER_URL + "/insert-hits/filtered")
    public ResponseEntity<PaginatedComposedDto> findToInsertHitsFiltered(@PathVariable long sectionId,
                                                                         @RequestBody Filter filter) {
        try {
            PaginatedComposedDto composedWithoutHits = composedService.findSummarizedWithoutHitsFiltered(sectionId, filter);
            if (composedWithoutHits == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithoutHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(COMPOSED_PRODUCTION_ORDER_URL + "/approval")
    public ResponseEntity<PaginatedComposedDto> findAllForApproval(@PathVariable long sectionId) {
        try {
            PaginatedComposedDto composedWithHits = composedService.findAllSummarizedWithHits(sectionId);
            if (composedWithHits == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(COMPOSED_PRODUCTION_ORDER_URL + "/approval/filtered")
    public ResponseEntity<PaginatedComposedDto> findForApprovalFiltered(@PathVariable long sectionId,
                                                                        @RequestBody Filter filter) {
        try {
            PaginatedComposedDto composedWithHits = composedService.findSummarizedWithHitsFiltered(sectionId, filter);
            if (composedWithHits == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedWithHits, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(COMPOSED_PRODUCTION_ORDER_URL + "/completed")
    public ResponseEntity<List<ComposedSummaryDto>> findAllCompleted(@PathVariable long sectionId) {
        try {
            List<ComposedSummaryDto> composedCompleted = composedService.findAllCompleted(sectionId);
            if (composedCompleted.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(COMPOSED_PRODUCTION_ORDER_URL + "/completed/filtered")
    public ResponseEntity<PaginatedComposedDto> findCompletedFiltered(@PathVariable long sectionId,
                                                                      @RequestBody Filter filter) {
        try {
            PaginatedComposedDto composedCompleted = composedService.findCompletedFiltered(sectionId, filter);
            if (composedCompleted == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(COMPOSED_PRODUCTION_ORDER_URL + "/production-orders")
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
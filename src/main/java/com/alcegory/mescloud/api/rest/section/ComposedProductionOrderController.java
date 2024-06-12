package com.alcegory.mescloud.api.rest.section;

<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
=======
import com.alcegory.mescloud.api.rest.base.SectionBaseController;
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
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
<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
            PaginatedComposedDto composedWithoutHits = composedService.findAllSummarizedWithoutHits();
=======
            PaginatedComposedDto composedWithoutHits = composedService.findAllSummarizedWithoutHits(sectionId);
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
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
<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
            PaginatedComposedDto composedWithoutHits = composedService.findSummarizedWithoutHitsFiltered(filter);
=======
            PaginatedComposedDto composedWithoutHits = composedService.findSummarizedWithoutHitsFiltered(sectionId, filter);
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
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
<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
            PaginatedComposedDto composedWithHits = composedService.findAllSummarizedWithHits();
=======
            PaginatedComposedDto composedWithHits = composedService.findAllSummarizedWithHits(sectionId);
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
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
<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
            PaginatedComposedDto composedWithHits = composedService.findSummarizedWithHitsFiltered(filter);
=======
            PaginatedComposedDto composedWithHits = composedService.findSummarizedWithHitsFiltered(sectionId, filter);
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
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
<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
            PaginatedComposedDto composedCompleted = composedService.findCompletedFiltered(filter);
=======
            PaginatedComposedDto composedCompleted = composedService.findCompletedFiltered(sectionId, filter);
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
            if (composedCompleted == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(composedCompleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/ComposedProductionOrderController.java
    @PostMapping("/production-orders")
=======
    @PostMapping(COMPOSED_PRODUCTION_ORDER_URL + "/production-orders")
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/ComposedProductionOrderController.java
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
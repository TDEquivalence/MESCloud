package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.api.rest.response.ErrorResponse;
import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.exception.InconsistentPropertiesException;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.SampleDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestSampleDto;
import com.alcegory.mescloud.service.composed.SampleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/sample")
@AllArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody RequestSampleDto requestSampleDto, Authentication authentication) {
        try {
            SampleDto sampleDto = sampleService.create(requestSampleDto, authentication);
            return ResponseEntity.ok(sampleDto);
        } catch (InconsistentPropertiesException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Inconsistent properties: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<SampleDto>> findAll() {
        try {
            List<SampleDto> sampleDtos = sampleService.getAll();
            return ResponseEntity.ok(sampleDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<List<ProductionOrderDto>> removeProductionOrderFromComposed(@RequestBody RequestById request,
                                                                                      Authentication authentication) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductionOrderDto> productionOrders = sampleService.removeProductionOrderFromComposed(request, authentication);
            return ResponseEntity.ok(productionOrders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
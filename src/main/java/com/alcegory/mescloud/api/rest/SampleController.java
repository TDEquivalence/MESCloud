package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.SampleDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestSampleDto;
import com.alcegory.mescloud.service.SampleService;
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
    public ResponseEntity<SampleDto> create(@RequestBody RequestSampleDto requestSampleDto, Authentication authentication) {
        SampleDto sampleDto = sampleService.create(requestSampleDto, authentication);
        if (sampleDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(sampleDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SampleDto>> findAll() {
        List<SampleDto> sampleDtos = sampleService.getAll();
        return new ResponseEntity<>(sampleDtos, HttpStatus.OK);
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
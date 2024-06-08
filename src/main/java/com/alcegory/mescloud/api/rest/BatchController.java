package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.model.dto.composed.BatchDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.request.RequestBatchDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestToRejectBatchDto;
import com.alcegory.mescloud.service.composed.BatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/batch")
@AllArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    public ResponseEntity<BatchDto> create(@RequestBody RequestBatchDto requestBatchDto, Authentication authentication) {
        try {
            BatchDto batchDto = batchService.create(requestBatchDto, authentication);

            if (batchDto == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(batchDto);
        } catch (ForbiddenAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BatchDto>> findAll() {
        try {
            List<BatchDto> batchDtos = batchService.getAll();
            if (batchDtos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(batchDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<BatchDto> rejected(@RequestBody RequestToRejectBatchDto requestBatchDto, Authentication authentication) {
        BatchDto batchDto = batchService.rejectComposed(requestBatchDto, authentication);
        if (batchDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(batchDto, HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<List<ProductionOrderDto>> removeHits(@RequestBody RequestById request, Authentication authentication) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductionOrderDto> productionOrders = batchService.removeBatch(request, authentication);
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
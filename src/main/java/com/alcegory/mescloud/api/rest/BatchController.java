package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.BatchDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.request.RequestBatchDto;
import com.alcegory.mescloud.model.request.RequestById;
import com.alcegory.mescloud.model.request.RequestToRejectBatchDto;
import com.alcegory.mescloud.service.BatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/batch")
@AllArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    public ResponseEntity<BatchDto> create(@RequestBody RequestBatchDto requestBatchDto) {
        BatchDto batchDto = batchService.create(requestBatchDto);
        if (batchDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(batchDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BatchDto>> findAll() {
        List<BatchDto> batchDtos = batchService.getAll();
        return new ResponseEntity<>(batchDtos, HttpStatus.OK);
    }

    @PostMapping("/reject")
    public ResponseEntity<BatchDto> rejected(@RequestBody RequestToRejectBatchDto requestBatchDto) {
        BatchDto batchDto = batchService.rejectComposed(requestBatchDto);
        if (batchDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(batchDto, HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<List<ProductionOrderDto>> removeHits(@RequestBody RequestById request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductionOrderDto> productionOrders = batchService.removeBatch(request);
            return ResponseEntity.ok(productionOrders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
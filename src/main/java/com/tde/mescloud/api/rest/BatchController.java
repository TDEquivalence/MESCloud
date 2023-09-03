package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.BatchDto;
import com.tde.mescloud.model.dto.RequestBatchDto;
import com.tde.mescloud.service.BatchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batch")
@AllArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    public ResponseEntity<BatchDto> createSample(@RequestBody RequestBatchDto requestBatchDto) {
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
}
package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.service.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counter-records")
@AllArgsConstructor
public class CounterRecordController {

    private final CounterRecordService service;
    private final CounterRecordConverter converter;
    

    @PostMapping("/filter")
    public ResponseEntity<List<CounterRecordDto>> findAllByCriteria(@RequestBody CounterRecordFilterDto filter) {
        List<CounterRecordDto> counterRecords = service.findAllByCriteria(filter);
        return new ResponseEntity<>(counterRecords, HttpStatus.OK);
    }

    @PostMapping("/completion")
    public ResponseEntity<List<CounterRecordDto>> getLastPerProductionOrder(@RequestBody CounterRecordFilterDto filter) {
        List<CounterRecordDto> counterRecords = service.findLastPerProductionOrder(filter);
        return new ResponseEntity<>(counterRecords, HttpStatus.OK);
    }
}
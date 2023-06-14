package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.service.CounterRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counter-records")
public class CounterRecordController {

    private final CounterRecordService service;
    private final CounterRecordConverter converter;

    public CounterRecordController(CounterRecordService service, CounterRecordConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @GetMapping
    public ResponseEntity<List<CounterRecordDto>> getCounterRecords() {
        List<CounterRecord> counterRecords = service.findAll();
        List<CounterRecordDto> counterRecordDto = converter.convertToDto(counterRecords);
        return new ResponseEntity<>(counterRecordDto, HttpStatus.OK);
    }

    @PostMapping("/completion")
    public ResponseEntity<List<CounterRecordDto>> getLastPerProductionOrder(@RequestBody CounterRecordFilterDto filter) {
        List<CounterRecord> counterRecords = service.findLastPerProductionOrder();
        List<CounterRecordDto> counterRecordDtos = converter.convertToDto(counterRecords);
        return new ResponseEntity<>(counterRecordDtos, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<CounterRecordDto>> filterCounterRecords(@RequestBody CounterRecordFilterDto filter) {
        List<CounterRecord> counterRecords = service.findAllByCriteria(filter);
        List<CounterRecordDto> counterRecordDtos = converter.convertToDto(counterRecords);
        return new ResponseEntity<>(counterRecordDtos, HttpStatus.OK);
    }
}
package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.service.CounterRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<CounterRecordDto> counterRecordDtos = converter.convertToDTO(counterRecords);
        return new ResponseEntity<>(counterRecordDtos, HttpStatus.OK);
    }
}

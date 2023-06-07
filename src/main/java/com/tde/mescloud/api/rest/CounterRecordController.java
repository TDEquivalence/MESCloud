package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.CounterRecord;
import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.CounterRecordOrderDto;
import com.tde.mescloud.model.dto.CounterRecordSearchDto;
import com.tde.mescloud.service.CounterRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<List<CounterRecordDto>> getCounterRecords(@RequestParam(required = false) Integer skip,
                                                                    @RequestParam(required = false) Integer take,
                                                                    @RequestParam(required = false) List<CounterRecordSearchDto> search,
                                                                    @RequestParam(required = false) List<CounterRecordOrderDto> order) {

        int test = skip;
        List<CounterRecord> counterRecords = service.findAll();
        List<CounterRecordDto> counterRecordDtos = converter.convertToDto(counterRecords);
        return new ResponseEntity<>(counterRecordDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<CounterRecordDto>> filterCounterRecords(@RequestBody CounterRecordFilterDto filter) {
        List<CounterRecordDto> counterRecordDtos = new ArrayList<>();
        return new ResponseEntity<>(counterRecordDtos, HttpStatus.OK);
    }
}

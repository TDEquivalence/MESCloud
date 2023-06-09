package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.converter.CounterRecordConverter;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.PaginatedCounterRecordsDto;
import com.tde.mescloud.service.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/counter-records")
@AllArgsConstructor
public class CounterRecordController {

    private final CounterRecordService service;
    private final CounterRecordConverter converter;


    @PostMapping("/filter")
    public ResponseEntity<PaginatedCounterRecordsDto> getFilteredAndPaginated(@RequestBody CounterRecordFilterDto filter) {
        PaginatedCounterRecordsDto paginatedCounterRecords = service.getFilteredAndPaginated(filter);
        return new ResponseEntity<>(paginatedCounterRecords, HttpStatus.OK);
    }

    @PostMapping("/completion")
    public ResponseEntity<PaginatedCounterRecordsDto> getLastPerProductionOrder(@RequestBody CounterRecordFilterDto filter) {
        PaginatedCounterRecordsDto paginatedCounterRecords = service.findLastPerProductionOrder(filter);
        return new ResponseEntity<>(paginatedCounterRecords, HttpStatus.OK);
    }
}
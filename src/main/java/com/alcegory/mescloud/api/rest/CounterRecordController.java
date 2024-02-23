package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.CounterRecordService;
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
public class
CounterRecordController {

    private final CounterRecordService service;

    @PostMapping("/filter")
    public ResponseEntity<PaginatedCounterRecordsDto> getFilteredAndPaginated(@RequestBody Filter filter) {
        PaginatedCounterRecordsDto paginatedCounterRecords = service.getFilteredAndPaginated(filter);
        return new ResponseEntity<>(paginatedCounterRecords, HttpStatus.OK);
    }

    @PostMapping("/completion")
    public ResponseEntity<PaginatedCounterRecordsDto> getLastPerProductionOrder(@RequestBody Filter filter) {
        PaginatedCounterRecordsDto paginatedCounterRecords = service.filterConclusionRecordsPaginated(filter);
        return new ResponseEntity<>(paginatedCounterRecords, HttpStatus.OK);
    }
}
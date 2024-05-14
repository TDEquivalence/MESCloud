package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.pagination.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.record.CounterRecordService;
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
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PaginatedCounterRecordsDto paginatedCounterRecords = service.getFilteredAndPaginated(filter);
            return ResponseEntity.ok(paginatedCounterRecords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/completion")
    public ResponseEntity<PaginatedCounterRecordsDto> getLastPerProductionOrder(@RequestBody Filter filter) {
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PaginatedCounterRecordsDto paginatedCounterRecords = service.filterConclusionRecordsPaginated(filter);
            return ResponseEntity.ok(paginatedCounterRecords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
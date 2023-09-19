package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.CounterRecordFilter;
import com.tde.mescloud.model.dto.CounterRecordSimplDto;
import com.tde.mescloud.model.dto.PaginatedCounterRecordsDto;
import com.tde.mescloud.service.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counter-records")
@AllArgsConstructor
public class
CounterRecordController {

    private final CounterRecordService service;

    @PostMapping("/filter")
    public ResponseEntity<PaginatedCounterRecordsDto> getFilteredAndPaginated(@RequestBody CounterRecordFilter filter) {
        PaginatedCounterRecordsDto paginatedCounterRecords = service.getFilteredAndPaginated(filter);
        return new ResponseEntity<>(paginatedCounterRecords, HttpStatus.OK);
    }

    @PostMapping("/completion")
    public ResponseEntity<PaginatedCounterRecordsDto> getLastPerProductionOrder(@RequestBody CounterRecordFilter filter) {
        PaginatedCounterRecordsDto paginatedCounterRecords = service.filterConclusionRecordsPaginated(filter);
        return new ResponseEntity<>(paginatedCounterRecords, HttpStatus.OK);
    }

    @GetMapping("/{id}/get-max-valid")
    public ResponseEntity<List<CounterRecordSimplDto>> findById(@PathVariable long id) {
        List<CounterRecordSimplDto> list = service.maxValid(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
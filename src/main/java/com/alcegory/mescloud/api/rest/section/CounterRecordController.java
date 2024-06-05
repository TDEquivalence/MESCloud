package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.model.dto.pagination.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.record.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class
CounterRecordController extends SectionBaseController {

    private static final String COUNTER_RECORDS_URL = "/counter-records";

    private final CounterRecordService service;

    @PostMapping(COUNTER_RECORDS_URL + "/filter")
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

    @PostMapping(COUNTER_RECORDS_URL + "/completion")
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
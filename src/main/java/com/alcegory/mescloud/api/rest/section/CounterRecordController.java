package com.alcegory.mescloud.api.rest.section;

<<<<<<< HEAD:src/main/java/com/alcegory/mescloud/api/rest/CounterRecordController.java
=======
import com.alcegory.mescloud.api.rest.base.SectionBaseController;
>>>>>>> test_environment:src/main/java/com/alcegory/mescloud/api/rest/section/CounterRecordController.java
import com.alcegory.mescloud.model.dto.pagination.PaginatedCounterRecordsDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.service.record.CounterRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<PaginatedCounterRecordsDto> getFilteredAndPaginated(@PathVariable long sectionId,
                                                                              @RequestBody Filter filter) {
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PaginatedCounterRecordsDto paginatedCounterRecords = service.getFilteredAndPaginated(sectionId, filter);
            return ResponseEntity.ok(paginatedCounterRecords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(COUNTER_RECORDS_URL + "/completion")
    public ResponseEntity<PaginatedCounterRecordsDto> getLastPerProductionOrder(@PathVariable long sectionId,
                                                                                @RequestBody Filter filter) {
        if (filter == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PaginatedCounterRecordsDto paginatedCounterRecords = service.filterConclusionRecordsPaginated(sectionId, filter);
            return ResponseEntity.ok(paginatedCounterRecords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
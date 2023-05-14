package com.tde.mescloud.api.rest;

import com.tde.mescloud.model.dto.CounterRecordDto;
import com.tde.mescloud.model.dto.CounterRecordQueryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/counter-records")
public class CounterRecordController {

    @GetMapping
    public ResponseEntity<CounterRecordDto[]> getCounterRecords(@RequestParam CounterRecordQueryDto counterRecordQuery) {
        return new ResponseEntity<>(new CounterRecordDto[0], HttpStatus.OK);
    }
}

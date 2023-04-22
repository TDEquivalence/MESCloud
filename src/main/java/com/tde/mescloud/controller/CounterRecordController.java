package com.tde.mescloud.controller;

import com.tde.mescloud.entity.CounterRecord;
import com.tde.mescloud.repository.CounterRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counter")
public class CounterRecordController {

    private final CounterRecordRepository repository;

    @Autowired
    public CounterRecordController(CounterRecordRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/add-counter")
    public String addCounterRecords(@RequestBody List<CounterRecord> counterRecords) {
        repository.saveAll(counterRecords);
        return "Counter Records inserted " + counterRecords.size();
    }

    @GetMapping("/counters")
    public List<CounterRecord> findAll() {
        return repository.findAll();
    }
}


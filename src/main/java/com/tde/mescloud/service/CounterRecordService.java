package com.tde.mescloud.service;

import com.tde.mescloud.model.CounterRecord;

import java.util.List;

public interface CounterRecordService {

    List<CounterRecord> save(List<CounterRecord> counterRecords);
}

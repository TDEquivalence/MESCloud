package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedCounterRecordsDto {
    private List<CounterRecordDto> counterRecords;
    private boolean hasNextPage;
}

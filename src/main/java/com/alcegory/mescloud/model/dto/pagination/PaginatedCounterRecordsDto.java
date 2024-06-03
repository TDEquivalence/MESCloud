package com.alcegory.mescloud.model.dto.pagination;

import com.alcegory.mescloud.model.dto.CounterRecordDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedCounterRecordsDto {
    private List<CounterRecordDto> counterRecords;
    private boolean hasNextPage;
}

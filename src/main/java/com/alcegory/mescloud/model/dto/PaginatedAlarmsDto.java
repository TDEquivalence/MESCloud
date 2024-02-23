package com.alcegory.mescloud.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedAlarmsDto {
    private List<AlarmDto> counterRecords;
    private boolean hasNextPage;
}

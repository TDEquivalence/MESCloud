package com.alcegory.mescloud.model.dto.pagination;

import com.alcegory.mescloud.model.dto.alarm.AlarmDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedAlarmsDto {
    private List<AlarmDto> counterRecords;
    private boolean hasNextPage;
}

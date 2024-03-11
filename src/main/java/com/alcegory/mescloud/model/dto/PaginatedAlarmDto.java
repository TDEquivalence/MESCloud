package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.model.entity.AlarmSummaryEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedAlarmDto {
    private List<AlarmSummaryEntity> alarms;
    private boolean hasNextPage;
}

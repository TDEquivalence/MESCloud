package com.alcegory.mescloud.model.dto.pagination;

import com.alcegory.mescloud.model.entity.alarm.AlarmSummaryEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedAlarmDto {
    private List<AlarmSummaryEntity> alarms;
    private boolean hasNextPage;
}

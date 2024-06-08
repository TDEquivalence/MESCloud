package com.alcegory.mescloud.model.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmCountsDto {

    private long totalAlarms;
    private long totalActiveAlarms;
}

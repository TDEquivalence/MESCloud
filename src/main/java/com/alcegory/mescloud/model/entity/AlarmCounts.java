package com.alcegory.mescloud.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmCounts {

    private long totalAlarms;
    private long totalActiveAlarms;
}

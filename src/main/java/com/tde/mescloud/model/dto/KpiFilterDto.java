package com.tde.mescloud.model.dto;

import lombok.Data;

//TODO: Should not extend CounterRecordFilter, it just needs the search bit
@Data
public class KpiFilterDto extends CounterRecordFilterDto {

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    private TimeMode timeMode;
}
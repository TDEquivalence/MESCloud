package com.tde.mescloud.model.dto;

import lombok.Data;

@Data
public class KpiFilterDto {

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    private TimeMode timeMode;
    private String startDate;
    private String endDate;
    private CounterRecordSearchDto[] search;
}
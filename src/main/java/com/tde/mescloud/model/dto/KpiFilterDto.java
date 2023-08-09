package com.tde.mescloud.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class KpiFilterDto {

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    private TimeMode timeMode;
    private Date startDate;
    private Date endDate;
    private CounterRecordSearchDto[] search;
}
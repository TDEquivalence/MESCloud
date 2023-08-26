package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tde.mescloud.model.dto.filter.FilterSearchDto;
import lombok.Setter;

@Setter
public class KpiFilterDto {

    private TimeMode timeMode;
    @JsonUnwrapped
    private FilterSearchDto<CounterRecordFilterDto.CounterRecordProperty> search;

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
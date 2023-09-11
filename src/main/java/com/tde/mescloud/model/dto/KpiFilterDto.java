package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tde.mescloud.model.filter.FilterSearch;
import com.tde.mescloud.model.filter.Searchable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KpiFilterDto implements Searchable<CounterRecordFilter.Property> {

    private TimeMode timeMode;
    @JsonUnwrapped
    private FilterSearch<CounterRecordFilter.Property> search;

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
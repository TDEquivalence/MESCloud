package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.utility.filter.FilterSearch;
import com.alcegory.mescloud.utility.filter.Searchable;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
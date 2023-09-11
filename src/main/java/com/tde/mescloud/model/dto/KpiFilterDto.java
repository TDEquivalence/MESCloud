package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tde.mescloud.model.winnow.FilterSearch;
import com.tde.mescloud.model.winnow.Searchable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KpiFilterDto implements Searchable<CounterRecordWinnow.Property> {

    private TimeMode timeMode;
    @JsonUnwrapped
    private FilterSearch<CounterRecordWinnow.Property> search;

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
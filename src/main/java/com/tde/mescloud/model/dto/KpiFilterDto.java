package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tde.mescloud.model.dto.winnow.WinnowSearch;
import com.tde.mescloud.model.dto.winnow.Searchable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KpiFilterDto implements Searchable<CounterRecordWinnow.Property> {

    private TimeMode timeMode;
    @JsonUnwrapped
    private WinnowSearch<CounterRecordWinnow.Property> search;

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
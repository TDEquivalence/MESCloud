package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.tde.mescloud.model.dto.filter.WinnowSearch;
import com.tde.mescloud.model.dto.filter.Searchable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KpiFilterDto implements Searchable<CounterRecordWinnowDto.CounterRecordProperty> {

    private TimeMode timeMode;
    @JsonUnwrapped
    private WinnowSearch<CounterRecordWinnowDto.CounterRecordProperty> search;

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
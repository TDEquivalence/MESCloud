package com.alcegory.mescloud.model.filter;

import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.utility.filter.FilterSearch;
import com.alcegory.mescloud.utility.filter.Searchable;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterDto implements Searchable<Filter.Property> {

    private TimeMode timeMode;
    @JsonUnwrapped
    private FilterSearch<Filter.Property> search;

    public enum TimeMode {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tde.mescloud.model.dto.CounterRecordFilterDto;

public interface Searchable {

    FilterSearch<CounterRecordFilterDto.CounterRecordProperty> getSearch();
}

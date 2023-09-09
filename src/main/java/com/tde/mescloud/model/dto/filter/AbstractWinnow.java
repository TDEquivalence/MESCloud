package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractWinnow<T extends WinnowProperty> implements Searchable<T>, Sortable<T> {

    @JsonUnwrapped
    private WinnowSearch<T> search;
    @JsonUnwrapped
    private WinnowSort<T> sort;
}

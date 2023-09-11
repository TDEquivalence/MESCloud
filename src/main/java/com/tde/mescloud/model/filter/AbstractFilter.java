package com.tde.mescloud.model.filter;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractFilter<T extends FilterProperty> implements Filter<T> {

    @JsonUnwrapped
    private FilterSearch<T> search;
    @JsonUnwrapped
    private FilterSort<T> sort;
}

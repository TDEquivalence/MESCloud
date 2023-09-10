package com.tde.mescloud.model.dto.winnow;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractWinnow<T extends WinnowProperty> implements Winnow<T> {

    @JsonUnwrapped
    private WinnowSearch<T> search;
    @JsonUnwrapped
    private WinnowSort<T> sort;
}

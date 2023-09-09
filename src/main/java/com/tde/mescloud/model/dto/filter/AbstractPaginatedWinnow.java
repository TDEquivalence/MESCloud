package com.tde.mescloud.model.dto.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractPaginatedWinnow<T extends WinnowProperty> extends AbstractWinnow<T> {

    private int take;
    private int skip;
}

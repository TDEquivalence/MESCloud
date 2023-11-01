package com.alcegory.mescloud.utility.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractPaginatedFilter<T extends FilterProperty> extends AbstractFilter<T> {

    private int take;
    private int skip;
}

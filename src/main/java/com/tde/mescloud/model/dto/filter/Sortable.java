package com.tde.mescloud.model.dto.filter;

public interface Sortable<T extends WinnowProperty> {

    WinnowSort<T> getSort();
}

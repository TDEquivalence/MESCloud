package com.tde.mescloud.model.dto.winnow;

public interface Sortable<T extends WinnowProperty> {

    WinnowSort<T> getSort();
}

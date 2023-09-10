package com.tde.mescloud.model.dto.winnow;

public interface Sortable<W extends WinnowProperty> {

    WinnowSort<W> getSort();
}

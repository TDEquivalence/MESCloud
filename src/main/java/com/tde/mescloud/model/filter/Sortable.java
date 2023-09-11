package com.tde.mescloud.model.filter;

public interface Sortable<W extends FilterProperty> {

    FilterSort<W> getSort();
}

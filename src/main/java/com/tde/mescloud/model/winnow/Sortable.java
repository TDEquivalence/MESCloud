package com.tde.mescloud.model.winnow;

public interface Sortable<W extends FilterProperty> {

    FilterSort<W> getSort();
}

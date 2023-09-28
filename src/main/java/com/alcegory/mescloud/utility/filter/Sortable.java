package com.alcegory.mescloud.utility.filter;

public interface Sortable<W extends FilterProperty> {

    FilterSort<W> getSort();
}

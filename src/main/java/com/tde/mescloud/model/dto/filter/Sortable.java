package com.tde.mescloud.model.dto.filter;

public interface Sortable<T extends SearchableProperty> {

    FilterSort<T> getSort();
}

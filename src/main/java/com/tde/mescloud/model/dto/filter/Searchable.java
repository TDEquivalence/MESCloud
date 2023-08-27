package com.tde.mescloud.model.dto.filter;

public interface Searchable<T extends SearchableProperty> {

    FilterSearch<T> getSearch();
}

package com.tde.mescloud.model.filter;

public interface Searchable<W extends FilterProperty> {

    FilterSearch<W> getSearch();
}

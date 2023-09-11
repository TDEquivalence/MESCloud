package com.tde.mescloud.model.winnow;

public interface Searchable<W extends FilterProperty> {

    FilterSearch<W> getSearch();
}

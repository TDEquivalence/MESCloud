package com.alcegory.mescloud.utility.filter;

public interface Searchable<W extends FilterProperty> {

    FilterSearch<W> getSearch();
}

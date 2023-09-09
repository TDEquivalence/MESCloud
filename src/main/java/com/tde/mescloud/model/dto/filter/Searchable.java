package com.tde.mescloud.model.dto.filter;

public interface Searchable<T extends WinnowProperty> {

    WinnowSearch<T> getSearch();
}

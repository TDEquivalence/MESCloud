package com.tde.mescloud.model.dto.winnow;

public interface Searchable<T extends WinnowProperty> {

    WinnowSearch<T> getSearch();
}

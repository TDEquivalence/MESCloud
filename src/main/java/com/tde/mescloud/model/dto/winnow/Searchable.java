package com.tde.mescloud.model.dto.winnow;

public interface Searchable<W extends WinnowProperty> {

    WinnowSearch<W> getSearch();
}

package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterSearch<T extends Enum<T>> {

    @JsonProperty("searchValueByName")
    private Map<T, String> searchValueByName;

    private Map<T, String> getSearchValueByName() {

        if (this.searchValueByName == null) {
            this.searchValueByName = new HashMap<>();
        }

        return this.searchValueByName;
    }

    public String getValue(T searchProperty) {
        return getSearchValueByName().get(searchProperty);
    }

    public Set<T> getKeys() {
        return getSearchValueByName().keySet();
    }
}

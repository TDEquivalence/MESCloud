package com.tde.mescloud.model.winnow;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterSearch<T extends FilterProperty> {

    @JsonProperty("searchValueByName")
    private final Map<T, String> searchValueByName = new HashMap<>();


    public String getValue(T searchProperty) {
        return searchValueByName.get(searchProperty);
    }

    public Set<T> getKeys() {
        return searchValueByName.keySet();
    }
}

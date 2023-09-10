package com.tde.mescloud.model.dto.winnow;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WinnowSearch<T extends WinnowProperty> {

    @JsonProperty("searchValueByName")
    private final Map<T, String> searchValueByName = new HashMap<>();


    public String getValue(T searchProperty) {
        return searchValueByName.get(searchProperty);
    }

    public Set<T> getKeys() {
        return searchValueByName.keySet();
    }
}

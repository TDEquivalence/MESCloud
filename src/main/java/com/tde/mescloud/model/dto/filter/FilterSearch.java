package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FilterSearch {

    @JsonProperty("searchValueByName")
    private Map<SearchableProperty, String> searchValueByName;

    private Map<SearchableProperty, String> getSearchValueByName() {

        if (this.searchValueByName == null) {
            this.searchValueByName = new HashMap<>();
        }

        return this.searchValueByName;
    }

    public String getValue(SearchableProperty searchProperty) {
        return getSearchValueByName().get(searchProperty);
    }

    public Set<SearchableProperty> getKeys() {
        return getSearchValueByName().keySet();
    }
}

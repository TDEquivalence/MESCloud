package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WinnowSort<T extends WinnowProperty> {

    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";

    @JsonProperty("sortValueByName")
    private Map<T, String> sortValueByName;


    private Map<T, String> getSortValueByName() {

        if (this.sortValueByName == null) {
            this.sortValueByName = new HashMap<>();
        }

        return this.sortValueByName;
    }

    public String getValue(T sortProperty) {
        return getSortValueByName().get(sortProperty);
    }

    public boolean isDescendingSort(T sortProperty) {
        return DESCENDING.equals(getValue(sortProperty));
    }

    public boolean isAscending(T sortProperty) {
        return ASCENDING.equals(getValue(sortProperty));
    }

    public Set<T> getKeys() {
        return getSortValueByName().keySet();
    }
}

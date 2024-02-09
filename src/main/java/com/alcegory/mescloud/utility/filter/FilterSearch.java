package com.alcegory.mescloud.utility.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Slf4j
@Getter
public class FilterSearch<T extends FilterProperty> {

    @JsonProperty("searchValueByName")
    private final Map<T, String> searchValueByName = new HashMap<>();

    public String getValue(T searchProperty) {
        return searchValueByName.get(searchProperty);
    }

    public Set<T> getKeys() {
        return searchValueByName.keySet();
    }

    public Timestamp getTimestampValue(T searchProperty) {

        if ("startDate".equals(searchProperty.getName()) || "endDate".equals(searchProperty.getName())) {
            String stringValue = getValue(searchProperty);

            if (stringValue != null) {

                try {
                    return Timestamp.valueOf(stringValue.replace("T", " ").replace("Z", ""));
                } catch (IllegalArgumentException e) {
                    log.error("Error parsing Timestamp for {}: {}", searchProperty.getName(), e.getMessage());
                }
            }

        }

        return null;
    }

    public void setSearchValueByName(T searchProperty, String value) {
        if (searchProperty != null) {
            searchValueByName.put(searchProperty, value);
        } else {
            log.warn("Attempted to set search value with null searchProperty.");
        }
    }
}

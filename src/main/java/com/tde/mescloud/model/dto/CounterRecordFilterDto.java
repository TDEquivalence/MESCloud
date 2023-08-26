package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CounterRecordFilterDto {

    private static final String DEFAULT_SORTING_VALUE = "DESCENDING";
    
    private int take;
    private int skip;
    private Map<CounterRecordProperty, String> searchValueByName;
    //TODO: change the Boolean value to a String value - ASCENDING OR DESCENDING to avoid nulls
    private Map<CounterRecordProperty, Boolean> sortDescendingByName;


    private Map<CounterRecordProperty, String> getSearchValueByName() {

        if (this.searchValueByName == null) {
            this.searchValueByName = new EnumMap<>(CounterRecordProperty.class);
        }

        return this.searchValueByName;
    }

    public String getSearchValue(CounterRecordProperty searchProperty) {
        return getSearchValueByName().get(searchProperty);
    }

    public Set<CounterRecordProperty> getSearchKeys() {
        return getSearchValueByName().keySet();
    }

    private Map<CounterRecordProperty, Boolean> getSortDescendingByName() {

        if (this.sortDescendingByName == null) {
            this.sortDescendingByName = new EnumMap<>(CounterRecordProperty.class);
        }

        return this.sortDescendingByName;
    }

    private Boolean getSortValue(CounterRecordProperty sortProperty) {
        return getSortDescendingByName().get(sortProperty);
    }

    public boolean isDescendingSort(CounterRecordProperty sortProperty) {
        Boolean isDescending = getSortValue(sortProperty);
        return isDescending == null || isDescending;
    }

    public Boolean isAscendingSort(CounterRecordProperty sortProperty) {
        return !isDescendingSort(sortProperty);
    }

    public Set<CounterRecordProperty> getSortKeys() {
        return getSortDescendingByName().keySet();
    }

    public enum CounterRecordProperty {
        PRODUCTION_ORDER_CODE("productionOrderCode"),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias"),
        EQUIPMENT_ALIAS("equipmentAlias"),
        AMOUNT("computedValue"),
        START_DATE("startDate"),
        END_DATE("endDate");

        private final String propertyName;

        CounterRecordProperty(String propertyName) {
            this.propertyName = propertyName;
        }

        @JsonValue
        public String getPropertyName() {
            return propertyName;
        }
    }
}
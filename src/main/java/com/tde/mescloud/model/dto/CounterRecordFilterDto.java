package com.tde.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tde.mescloud.model.dto.filter.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterRecordFilterDto implements Searchable<CounterRecordFilterDto.CounterRecordProperty>,
        Sortable<CounterRecordFilterDto.CounterRecordProperty> {

    private static final String DEFAULT_SORTING_VALUE = "DESCENDING";

    private int take;
    private int skip;
    @JsonUnwrapped
    private FilterSearch<CounterRecordProperty> search;
    @JsonUnwrapped
    private FilterSort<CounterRecordProperty> sort;

    public enum CounterRecordProperty implements SearchableProperty {
        //TODO: These values are the same used in the Repo. Replace by Constants & think about defining the searchable properties
        //through another way.
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
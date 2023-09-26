package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.filter.AbstractPaginatedFilter;
import com.tde.mescloud.model.filter.FilterDataTypeOperation;
import com.tde.mescloud.model.filter.FilterProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterRecordFilter extends AbstractPaginatedFilter<CounterRecordFilter.Property> {

    private static final String REGISTERED_AT_PROP = "registeredAt";

    @AllArgsConstructor
    @Getter
    public enum Property implements FilterProperty {

        REGISTERED_AT(REGISTERED_AT_PROP),
        PRODUCTION_ORDER_CODE("productionOrderCode", FilterDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias", FilterDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_ALIAS("equipmentAlias", FilterDataTypeOperation.STRING_EQUAL),
        AMOUNT("computedValue", FilterDataTypeOperation.INTEGER_GREATER_OR_EQUAL),
        START_DATE("startDate", REGISTERED_AT_PROP, FilterDataTypeOperation.DATE_GREATER_OR_EQUAL),
        END_DATE("endDate", REGISTERED_AT_PROP, FilterDataTypeOperation.DATE_LESS_OR_EQUAL);

        Property(String name, FilterDataTypeOperation dataTypeOperation) {
            this.name = name;
            this.entityProperty = name;
            this.dataTypeOperation = dataTypeOperation;
        }

        Property(String name) {
            this.name = name;
            this.entityProperty = name;
            this.dataTypeOperation = FilterDataTypeOperation.NONE;
        }

        private final String name;
        private final String entityProperty;
        private final FilterDataTypeOperation dataTypeOperation;
    }
}
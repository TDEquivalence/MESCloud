package com.alcegory.mescloud.model.filter;

import com.alcegory.mescloud.utility.filter.AbstractPaginatedFilter;
import com.alcegory.mescloud.utility.filter.FilterDataTypeOperation;
import com.alcegory.mescloud.utility.filter.FilterProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter extends AbstractPaginatedFilter<Filter.Property> {

    private static final String REGISTERED_AT_PROP = "registeredAt";
    private static final String CREATED_AT_PROP = "createdAt";

    @AllArgsConstructor
    @Getter
    public enum Property implements FilterProperty {

        REGISTERED_AT(REGISTERED_AT_PROP),
        PRODUCTION_ORDER_CODE("productionOrderCode", FilterDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias", FilterDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_ALIAS("equipmentAlias", FilterDataTypeOperation.STRING_EQUAL),
        AMOUNT("computedValue", FilterDataTypeOperation.INTEGER_GREATER_OR_EQUAL),
        START_DATE("startDate", REGISTERED_AT_PROP, FilterDataTypeOperation.DATE_GREATER_OR_EQUAL),
        END_DATE("endDate", REGISTERED_AT_PROP, FilterDataTypeOperation.DATE_LESS_OR_EQUAL),

        //User Filter
        FIRST_NAME("firstName", "firstName", FilterDataTypeOperation.STRING_EQUAL),
        LAST_NAME("lastName", "lastName", FilterDataTypeOperation.STRING_EQUAL),
        EMAIL("email", "email", FilterDataTypeOperation.STRING_EQUAL),
        ROLE("role", "role", FilterDataTypeOperation.STRING_EQUAL),

        //Alarm
        STATUS("status", FilterDataTypeOperation.STRING_EQUAL),
        COMPLETED_AT("completedAt", FilterDataTypeOperation.DATE_LESS_OR_EQUAL),
        RECOGNIZED_AT("recognizedAt", FilterDataTypeOperation.DATE_LESS_OR_EQUAL);


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
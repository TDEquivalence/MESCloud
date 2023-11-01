package com.alcegory.mescloud.model.filter;


import com.alcegory.mescloud.utility.filter.AbstractFilter;
import com.alcegory.mescloud.utility.filter.FilterDataTypeOperation;
import com.alcegory.mescloud.utility.filter.FilterProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmFilter extends AbstractFilter<AlarmFilter.Property> {

    private static final String CREATED_AT_PROP = "createdAt";

    @AllArgsConstructor
    @Getter
    public enum Property implements FilterProperty {

        STATUS("status", FilterDataTypeOperation.STRING_EQUAL),
        START_DATE("startDate", CREATED_AT_PROP, FilterDataTypeOperation.DATE_GREATER_OR_EQUAL),
        END_DATE("endDate", CREATED_AT_PROP, FilterDataTypeOperation.DATE_LESS_OR_EQUAL),
        COMPLETED_AT("completedAt", FilterDataTypeOperation.DATE_LESS_OR_EQUAL),
        RECOGNIZED_AT("recognizedAt", FilterDataTypeOperation.DATE_LESS_OR_EQUAL);

        Property(String name, FilterDataTypeOperation dataTypeOperation) {
            this.name = name;
            this.entityProperty = name;
            this.dataTypeOperation = dataTypeOperation;
        }

        private final String name;
        private final String entityProperty;
        private final FilterDataTypeOperation dataTypeOperation;
    }
}

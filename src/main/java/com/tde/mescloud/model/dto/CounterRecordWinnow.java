package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.dto.filter.AbstractPaginatedWinnow;
import com.tde.mescloud.model.dto.filter.WinnowDataTypeOperation;
import com.tde.mescloud.model.dto.filter.WinnowProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterRecordWinnow extends AbstractPaginatedWinnow<CounterRecordWinnow.Property> {

    private static final String DEFAULT_SORTING_VALUE = "DESCENDING";

    @AllArgsConstructor
    @Getter
    public enum Property implements WinnowProperty {

        PRODUCTION_ORDER_CODE("productionOrderCode", WinnowDataTypeOperation.STRING),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias", WinnowDataTypeOperation.STRING),
        EQUIPMENT_ALIAS("equipmentAlias", WinnowDataTypeOperation.STRING),
        AMOUNT("computedValue", WinnowDataTypeOperation.INTEGER_GREATER_OR_EQUAL),
        START_DATE("startDate", WinnowDataTypeOperation.DATE_GREATER_OR_EQUAL),
        END_DATE("endDate", WinnowDataTypeOperation.DATE_LESS_OR_EQUAL);

        private final String name;
        private final WinnowDataTypeOperation dataTypeOperation;
    }
}
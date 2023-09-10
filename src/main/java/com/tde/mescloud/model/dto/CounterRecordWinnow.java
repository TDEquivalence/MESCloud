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

        PRODUCTION_ORDER_CODE("productionOrderCode", "productionOrderCode", WinnowDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias", "equipmentOutputAlias", WinnowDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_ALIAS("equipmentAlias", "equipmentAlias", WinnowDataTypeOperation.STRING_EQUAL),
        AMOUNT("computedValue", "computedValue", WinnowDataTypeOperation.INTEGER_GREATER_OR_EQUAL),
        START_DATE("startDate", "registeredAt", WinnowDataTypeOperation.DATE_GREATER_OR_EQUAL),
        END_DATE("endDate", "registeredAt", WinnowDataTypeOperation.DATE_LESS_OR_EQUAL);

        private final String name;
        private final String entityProperty;
        private final WinnowDataTypeOperation dataTypeOperation;
    }
}
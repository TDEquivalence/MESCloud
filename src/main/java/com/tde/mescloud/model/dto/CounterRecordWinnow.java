package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.dto.filter.AbstractPaginatedWinnow;
import com.tde.mescloud.model.dto.filter.WinnowDataType;
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

        PRODUCTION_ORDER_CODE("productionOrderCode", WinnowDataType.STRING),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias", WinnowDataType.STRING),
        EQUIPMENT_ALIAS("equipmentAlias", WinnowDataType.STRING),
        AMOUNT("computedValue", WinnowDataType.INTEGER),
        START_DATE("startDate", WinnowDataType.DATE),
        END_DATE("endDate", WinnowDataType.DATE);

        private final String name;
        private final WinnowDataType dataType;
    }
}
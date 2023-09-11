package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.winnow.AbstractPaginatedFilter;
import com.tde.mescloud.model.winnow.FilterDataTypeOperation;
import com.tde.mescloud.model.winnow.FilterProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterRecordWinnow extends AbstractPaginatedFilter<CounterRecordWinnow.Property> {

    @AllArgsConstructor
    @Getter
    public enum Property implements FilterProperty {

        PRODUCTION_ORDER_CODE("productionOrderCode", "productionOrderCode", FilterDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_OUTPUT_ALIAS("equipmentOutputAlias", "equipmentOutputAlias", FilterDataTypeOperation.STRING_EQUAL),
        EQUIPMENT_ALIAS("equipmentAlias", "equipmentAlias", FilterDataTypeOperation.STRING_EQUAL),
        AMOUNT("computedValue", "computedValue", FilterDataTypeOperation.INTEGER_GREATER_OR_EQUAL),
        START_DATE("startDate", "registeredAt", FilterDataTypeOperation.DATE_GREATER_OR_EQUAL),
        END_DATE("endDate", "registeredAt", FilterDataTypeOperation.DATE_LESS_OR_EQUAL);

        private final String name;
        private final String entityProperty;
        private final FilterDataTypeOperation dataTypeOperation;
    }
}
package com.tde.mescloud.model.filter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface FilterProperty {

    @JsonValue
    String getName();

    String getEntityProperty();

    FilterDataTypeOperation getDataTypeOperation();
}

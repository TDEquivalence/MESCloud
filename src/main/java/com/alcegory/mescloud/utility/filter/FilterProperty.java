package com.alcegory.mescloud.utility.filter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface FilterProperty {

    @JsonValue
    String getName();

    String getEntityProperty();

    FilterDataTypeOperation getDataTypeOperation();
}

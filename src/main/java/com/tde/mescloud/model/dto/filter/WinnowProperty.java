package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface WinnowProperty {

    @JsonValue
    String getName();

    WinnowDataTypeOperation getDataTypeOperation();
}

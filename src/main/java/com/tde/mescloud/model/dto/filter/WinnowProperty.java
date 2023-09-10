package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface WinnowProperty {

    @JsonValue
    String getName();

    String getEntityProperty();

    WinnowDataTypeOperation getDataTypeOperation();
}

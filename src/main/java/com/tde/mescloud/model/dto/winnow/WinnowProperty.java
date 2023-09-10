package com.tde.mescloud.model.dto.winnow;

import com.fasterxml.jackson.annotation.JsonValue;

public interface WinnowProperty {

    @JsonValue
    String getName();

    String getEntityProperty();

    WinnowDataTypeOperation getDataTypeOperation();
}

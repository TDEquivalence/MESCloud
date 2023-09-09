package com.tde.mescloud.model.dto.filter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface WinnowProperty {

    String jj = "";
    
    @JsonValue
    String getName();

    WinnowDataType getDataType();
}

package com.alcegory.mescloud.azure.model.constant;

import lombok.Getter;

@Getter
public enum Status {

    RECEIVED("RECEIVED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    Status(String value) {
        this.value = value;
    }
}

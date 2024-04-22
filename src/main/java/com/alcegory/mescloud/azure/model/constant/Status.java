package com.alcegory.mescloud.azure.model.constant;

import lombok.Getter;

@Getter
public enum Status {

    INITIAL("INITIAL"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    Status(String value) {
        this.value = value;
    }
}

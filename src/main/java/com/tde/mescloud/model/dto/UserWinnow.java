package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.dto.winnow.AbstractWinnow;
import com.tde.mescloud.model.dto.winnow.WinnowDataTypeOperation;
import com.tde.mescloud.model.dto.winnow.WinnowProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserWinnow extends AbstractWinnow<UserWinnow.Property> {

    @AllArgsConstructor
    @Getter
    public enum Property implements WinnowProperty {

        FIRST_NAME("firstName", "firstName", WinnowDataTypeOperation.STRING_EQUAL),
        LAST_NAME("lastName", "lastName", WinnowDataTypeOperation.STRING_EQUAL),
        EMAIL("email", "email", WinnowDataTypeOperation.STRING_EQUAL),
        ROLE("role", "role", WinnowDataTypeOperation.STRING_EQUAL);

        private final String name;
        private final String entityProperty;
        private final WinnowDataTypeOperation dataTypeOperation;
    }
}

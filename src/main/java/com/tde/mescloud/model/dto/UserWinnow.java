package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.winnow.AbstractFilter;
import com.tde.mescloud.model.winnow.FilterDataTypeOperation;
import com.tde.mescloud.model.winnow.FilterProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserWinnow extends AbstractFilter<UserWinnow.Property> {

    @AllArgsConstructor
    @Getter
    public enum Property implements FilterProperty {

        FIRST_NAME("firstName", "firstName", FilterDataTypeOperation.STRING_EQUAL),
        LAST_NAME("lastName", "lastName", FilterDataTypeOperation.STRING_EQUAL),
        EMAIL("email", "email", FilterDataTypeOperation.STRING_EQUAL),
        ROLE("role", "role", FilterDataTypeOperation.STRING_EQUAL);

        private final String name;
        private final String entityProperty;
        private final FilterDataTypeOperation dataTypeOperation;
    }
}

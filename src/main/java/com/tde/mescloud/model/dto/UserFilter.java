package com.tde.mescloud.model.dto;

import com.tde.mescloud.model.filter.AbstractFilter;
import com.tde.mescloud.model.filter.FilterDataTypeOperation;
import com.tde.mescloud.model.filter.FilterProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserFilter extends AbstractFilter<UserFilter.Property> {

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

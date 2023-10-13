package com.alcegory.mescloud.model.filter;

import com.alcegory.mescloud.utility.filter.AbstractFilter;
import com.alcegory.mescloud.utility.filter.FilterDataTypeOperation;
import com.alcegory.mescloud.utility.filter.FilterProperty;
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

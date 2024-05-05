package com.alcegory.mescloud.security.model;

import lombok.Getter;

@Getter
public enum SectionAuthority {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    ANALYST_READ("manager:read"),
    
    OPERATOR_READ("operator:read"),
    OPERATOR_UPDATE("operator:update"),
    OPERATOR_CREATE("operator:create"),
    OPERATOR_DELETE("operator:delete");

    private final String permission;

    SectionAuthority(String permission) {
        this.permission = permission;
    }
}

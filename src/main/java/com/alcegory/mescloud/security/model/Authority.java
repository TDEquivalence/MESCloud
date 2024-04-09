package com.alcegory.mescloud.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Authority {

    SUPER_ADMIN_READ("super_admin:read"),
    SUPER_ADMIN_UPDATE("super_admin:update"),
    SUPER_ADMIN_CREATE("super_admin:create"),
    SUPER_ADMIN_DELETE("super_admin:delete"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    OPERATOR_READ("operator:read"),
    OPERATOR_UPDATE("operator:update"),
    OPERATOR_CREATE("operator:create"),
    OPERATOR_DELETE("operator:delete");

    @Getter
    private final String permission;
}

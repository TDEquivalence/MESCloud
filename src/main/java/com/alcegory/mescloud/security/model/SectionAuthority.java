package com.alcegory.mescloud.security.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public enum SectionAuthority {

    SUPER_ADMIN(
            "super_admin:read",
            "super_admin:update",
            "super_admin:create",
            "super_admin:delete"
    ),
    ADMIN(
            "admin:read",
            "admin:update",
            "admin:create",
            "admin:delete"
    ),
    OPERATOR(
            "operator:read",
            "operator:update",
            "operator:create",
            "operator:delete"
    );

    private final Set<String> permissions;

    SectionAuthority(String... permissions) {
        this.permissions = new HashSet<>(Arrays.asList(permissions));
    }

    public String getPermissions() {
        return permissions.toString();
    }
}

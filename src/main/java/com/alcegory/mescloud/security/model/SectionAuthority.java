package com.alcegory.mescloud.security.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public enum SectionAuthority {

    SUPER_ADMIN(
            SectionPermissions.SUPER_ADMIN_READ.getPermission(),
            SectionPermissions.SUPER_ADMIN_UPDATE.getPermission(),
            SectionPermissions.SUPER_ADMIN_CREATE.getPermission(),
            SectionPermissions.SUPER_ADMIN_DELETE.getPermission()
    ),
    ADMIN(
            SectionPermissions.ADMIN_READ.getPermission(),
            SectionPermissions.ADMIN_UPDATE.getPermission(),
            SectionPermissions.ADMIN_CREATE.getPermission(),
            SectionPermissions.ADMIN_DELETE.getPermission()
    ),
    OPERATOR(
            SectionPermissions.OPERATOR_READ.getPermission(),
            SectionPermissions.OPERATOR_UPDATE.getPermission(),
            SectionPermissions.OPERATOR_CREATE.getPermission(),
            SectionPermissions.OPERATOR_DELETE.getPermission()
    );

    private final Set<String> permissions;

    SectionAuthority(String... permissions) {
        this.permissions = new HashSet<>(Arrays.asList(permissions));
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}


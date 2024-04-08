package com.alcegory.mescloud.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {

    SUPER_ADMIN(Set.of(
            Authority.SUPER_ADMIN_READ,
            Authority.SUPER_ADMIN_CREATE,
            Authority.SUPER_ADMIN_UPDATE,
            Authority.SUPER_ADMIN_DELETE,
            Authority.ADMIN_READ,
            Authority.ADMIN_CREATE,
            Authority.ADMIN_UPDATE,
            Authority.ADMIN_DELETE,
            Authority.OPERATOR_READ,
            Authority.OPERATOR_CREATE,
            Authority.OPERATOR_UPDATE,
            Authority.OPERATOR_DELETE
    )),
    ADMIN(Set.of(
            Authority.ADMIN_READ,
            Authority.ADMIN_CREATE,
            Authority.ADMIN_UPDATE,
            Authority.ADMIN_DELETE
    )),
    OPERATOR(Set.of(
            Authority.OPERATOR_READ,
            Authority.OPERATOR_CREATE,
            Authority.OPERATOR_UPDATE,
            Authority.OPERATOR_DELETE
    ));

    @Getter
    private final Set<Authority> authorities;

    public List<SimpleGrantedAuthority> getRoleAuthorities() {
        var grantedAuthorities = getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toList());
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return grantedAuthorities;
    }
}

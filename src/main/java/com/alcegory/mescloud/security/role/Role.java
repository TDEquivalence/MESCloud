package com.alcegory.mescloud.security.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {

    OPERATOR(Set.of(Authority.USER_READ, Authority.USER_UPDATE, Authority.USER_DELETE, Authority.USER_CREATE)),
    ADMIN(Set.of(Authority.ADMIN_READ, Authority.ADMIN_UPDATE, Authority.ADMIN_DELETE, Authority.ADMIN_CREATE)),
    SUPER_ADMIN(Set.of(Authority.ADMIN_READ, Authority.ADMIN_UPDATE, Authority.ADMIN_DELETE, Authority.ADMIN_CREATE));

    @Getter
    private final Set<Authority> authorities;

    public List<SimpleGrantedAuthority> getRoleAuthorities() {
        var grantedAuthorities = getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toList());
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return grantedAuthorities;
    }
}

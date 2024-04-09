package com.alcegory.mescloud.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum SectionRole {

    SUPER_ADMIN(Set.of(
            SectionAuthority.SUPER_ADMIN_READ,
            SectionAuthority.SUPER_ADMIN_UPDATE,
            SectionAuthority.SUPER_ADMIN_CREATE,
            SectionAuthority.SUPER_ADMIN_DELETE,
            SectionAuthority.ADMIN_READ,
            SectionAuthority.ADMIN_UPDATE,
            SectionAuthority.ADMIN_CREATE,
            SectionAuthority.ADMIN_DELETE
    )),
    ADMIN(Set.of(
            SectionAuthority.ADMIN_READ,
            SectionAuthority.ADMIN_UPDATE,
            SectionAuthority.ADMIN_CREATE,
            SectionAuthority.ADMIN_DELETE,
            SectionAuthority.OPERATOR_READ,
            SectionAuthority.OPERATOR_UPDATE,
            SectionAuthority.OPERATOR_CREATE,
            SectionAuthority.OPERATOR_DELETE
    )),
    OPERATOR(Set.of(
            SectionAuthority.OPERATOR_READ,
            SectionAuthority.OPERATOR_UPDATE,
            SectionAuthority.OPERATOR_CREATE,
            SectionAuthority.OPERATOR_DELETE
    ));

    @Getter
    private final Set<SectionAuthority> authorities;

    public List<SimpleGrantedAuthority> getRoleAuthorities() {
        var grantedAuthorities = getAuthorities()
                .stream()
                .map(sectionAuthority -> new SimpleGrantedAuthority(sectionAuthority.getPermission()))
                .collect(Collectors.toList());
        grantedAuthorities.add(new SimpleGrantedAuthority("SECTION_ROLE_" + this.name()));
        return grantedAuthorities;
    }
}


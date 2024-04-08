package com.alcegory.mescloud.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "role")
@NoArgsConstructor
public class SectionRoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Authority.class)
    private Set<Authority> authorities;

    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        for (SectionAuthority sectionAuthority : SectionAuthority.values()) {
            if (sectionAuthority.name().equals(name)) {
                permissions.add(sectionAuthority.getPermissions());
            }
        }
        return permissions;
    }
}


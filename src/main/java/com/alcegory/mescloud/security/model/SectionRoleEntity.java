package com.alcegory.mescloud.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "role")
@AllArgsConstructor
@NoArgsConstructor
public class SectionRoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SectionAuthority name;

    public Set<String> getPermissions() {
        if (name == null) {
            return new HashSet<>();
        }
        return name.getPermissions();
    }
}


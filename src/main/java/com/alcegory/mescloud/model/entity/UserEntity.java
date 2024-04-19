package com.alcegory.mescloud.model.entity;

import com.alcegory.mescloud.security.model.Role;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users")
public class UserEntity implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    private SectionRoleEntity sectionRole;

    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;
    private boolean isNotLocked;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getRoleAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}

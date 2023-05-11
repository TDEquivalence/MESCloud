package com.tde.mescloud.model.entity;

import com.tde.mescloud.security.model.token.TokenEntity;
import com.tde.mescloud.security.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users")
public class UserEntity implements UserDetails {

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
    private String[] userAuthorities;

    @OneToMany(mappedBy = "user")
    private transient  List<TokenEntity> tokens;

    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;
    private boolean isNotLocked;

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

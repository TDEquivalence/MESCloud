package com.tde.mescloud.security.auth;

import com.tde.mescloud.security.role.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private Role role;
}

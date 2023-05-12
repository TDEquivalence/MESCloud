package com.tde.mescloud.security.model.auth;

import com.tde.mescloud.security.role.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
}

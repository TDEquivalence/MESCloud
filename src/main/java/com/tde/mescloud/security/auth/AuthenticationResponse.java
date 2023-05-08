package com.tde.mescloud.security.auth;

import com.tde.mescloud.security.role.Role;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Date createdAt;
    private Role role;
}

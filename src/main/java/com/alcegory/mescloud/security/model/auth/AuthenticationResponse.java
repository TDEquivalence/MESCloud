package com.alcegory.mescloud.security.model.auth;

import com.alcegory.mescloud.security.model.Role;
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

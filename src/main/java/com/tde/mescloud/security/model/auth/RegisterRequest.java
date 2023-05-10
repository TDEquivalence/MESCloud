package com.tde.mescloud.security.model.auth;

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
    private String role;
}

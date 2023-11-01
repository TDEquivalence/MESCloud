package com.alcegory.mescloud.security.model.auth;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequest {

    private String username;
    private String password;
}

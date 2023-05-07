package com.tde.mescloud.security.auth;

import lombok.*;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private HttpHeaders headers;
}

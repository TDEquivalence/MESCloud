package com.tde.mescloud.security.model.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Europe/Lisbon")
    private Date createdAt;
}

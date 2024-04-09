package com.alcegory.mescloud.security.model.auth;

import com.alcegory.mescloud.security.model.Role;
import com.alcegory.mescloud.security.model.SectionRole;
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
    private SectionRole sectionRole;
}

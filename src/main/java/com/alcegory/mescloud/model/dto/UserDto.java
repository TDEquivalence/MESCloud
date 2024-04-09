package com.alcegory.mescloud.model.dto;

import com.alcegory.mescloud.security.model.Role;
import com.alcegory.mescloud.security.model.SectionRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Role role;
    private SectionRole sectionRole;
}

package com.alcegory.mescloud.model.dto.user;

import com.alcegory.mescloud.model.dto.company.SectionRoleMapping;
import com.alcegory.mescloud.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserConfigToUpdateDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private List<SectionRoleMapping> sectionRoles;
}
